package loadsight.loadsightserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import loadsight.loadsightserver.domain.loadtest.entity.TestRun;
import loadsight.loadsightserver.dto.GeneratorRunRequest;
import loadsight.loadsightserver.repository.TestRunExecutionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TestRunExecutionService {

    private final TestRunExecutionRepository repository;
    private final ObjectMapper objectMapper;
    private final String callbackBaseUrl;
    private final String callbackToken;

    public TestRunExecutionService(
            TestRunExecutionRepository repository,
            ObjectMapper objectMapper,
            @Value("${loadsight.callback-base-url:http://localhost:8084}") String callbackBaseUrl,
            @Value("${loadsight.generator.callback-token:change-me}") String callbackToken
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.callbackBaseUrl = callbackBaseUrl;
        this.callbackToken = callbackToken;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GeneratorRunRequest prepare(UUID runId) {
        TestRun run = findRun(runId);
        run.markStarting();
        return toGeneratorRequest(run);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markRunning(UUID runId) {
        findRun(runId).markRunning(OffsetDateTime.now());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(UUID runId, String message) {
        repository.findById(runId).ifPresent(run -> run.markFailed(message));
    }

    private TestRun findRun(UUID runId) {
        return repository.findById(runId)
                .orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));
    }

    private GeneratorRunRequest toGeneratorRequest(TestRun run) {
        JsonNode config = run.getConfigSnapshot();
        Map<String, String> headers = stringMap(config.path("headers"));
        Map<String, String> queryParams = new LinkedHashMap<>();
        applyAuthentication(config, headers, queryParams);

        JsonNode jsonBody = null;
        String content = null;
        String requestBody = textOrNull(config.path("requestBody"));
        if (requestBody != null) {
            try {
                jsonBody = objectMapper.readTree(requestBody);
            } catch (JsonProcessingException ignored) {
                content = requestBody;
            }
        }

        GeneratorRunRequest.Scenario scenario = new GeneratorRunRequest.Scenario(
                config.path("method").asText("GET"),
                headers,
                queryParams,
                jsonBody,
                content,
                Math.max(config.path("timeoutMs").asInt(5000), 1) / 1000.0
        );

        return new GeneratorRunRequest(
                run.getId().toString(),
                config.path("url").asText(),
                config.path("durationSec").asDouble(30),
                config.path("targetRps").asDouble(10),
                config.path("concurrency").asInt(10),
                config.path("maxP95Ms").asInt(1000),
                20,
                20,
                scenario,
                callbackBaseUrl + "/api/internal/v1/runs/" + run.getId() + "/result",
                callbackToken
        );
    }

    private void applyAuthentication(
            JsonNode config,
            Map<String, String> headers,
            Map<String, String> queryParams
    ) {
        String authType = config.path("authentication").asText("NONE");
        JsonNode auth = config.path("authConfig");

        switch (authType) {
            case "BEARER" -> headers.put("Authorization", "Bearer " + auth.path("token").asText());
            case "BASIC" -> {
                String credential = auth.path("username").asText() + ":" + auth.path("password").asText();
                headers.put(
                        "Authorization",
                        "Basic " + Base64.getEncoder().encodeToString(credential.getBytes(StandardCharsets.UTF_8))
                );
            }
            case "API_KEY" -> {
                String key = auth.path("key").asText("X-API-Key");
                String value = auth.path("value").asText();
                if (auth.path("location").asText("HEADER").equalsIgnoreCase("QUERY")) {
                    queryParams.put(key, value);
                } else {
                    headers.put(key, value);
                }
            }
            case "CUSTOM_HEADER" -> headers.put(
                    auth.path("key").asText("Authorization"),
                    auth.path("value").asText()
            );
            default -> {
            }
        }
    }

    private Map<String, String> stringMap(JsonNode node) {
        Map<String, String> values = new LinkedHashMap<>();
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> values.put(entry.getKey(), entry.getValue().asText()));
        }
        return values;
    }

    private String textOrNull(JsonNode node) {
        return node.isMissingNode() || node.isNull() || node.asText().isBlank() ? null : node.asText();
    }
}
