package loadsight.loadsightserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import loadsight.loadsightserver.domain.auth.entity.AppUser;
import loadsight.loadsightserver.domain.loadtest.entity.TestPlan;
import loadsight.loadsightserver.dto.TestPlanCreateRequest;
import loadsight.loadsightserver.dto.TestPlanCreateResponse;
import loadsight.loadsightserver.repository.TestPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TestPlanService {

    private final TestPlanRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TestPlanService(TestPlanRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TestPlanCreateResponse create(TestPlanCreateRequest request, UUID userId) {
        AppUser user = repository.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user does not exist."));

        TestPlan testPlan = TestPlan.builder()
                .owner(user)
                .createdBy(user)
                .name(request.name())
                .description(request.description())
                .httpMethod(request.method())
                .targetUrl(request.url())
                .requestHeaders(parseHeaders(request.headers()))
                .requestBody(request.requestBody())
                .authType(request.authentication())
                .authConfigEncrypted(parseAuthConfig(request.authConfig()))
                .timeoutMs(request.timeoutMs())
                .targetRps(request.targetRps())
                .durationSeconds(request.durationSec())
                .concurrency(request.concurrency())
                .maxErrorRate(request.maxErrorRate())
                .maxP95Ms(request.maxP95Ms())
                .build();

        return TestPlanCreateResponse.from(repository.save(testPlan));
    }

    private Map<String, Object> parseHeaders(Object headers) {
        if (headers == null) {
            return new LinkedHashMap<>();
        }

        JsonNode headerNode;
        if (headers instanceof String text) {
            if (text.isBlank()) {
                return new LinkedHashMap<>();
            }
            try {
                headerNode = objectMapper.readTree(text);
            } catch (JsonProcessingException exception) {
                throw new IllegalArgumentException("headers must be a valid JSON object.");
            }
        } else {
            headerNode = objectMapper.valueToTree(headers);
        }

        if (!headerNode.isObject()) {
            throw new IllegalArgumentException("headers must be a valid JSON object.");
        }

        return objectMapper.convertValue(
                headerNode,
                objectMapper.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Object.class)
        );
    }

    private JsonNode parseAuthConfig(Object authConfig) {
        if (authConfig == null) {
            return JsonNodeFactory.instance.objectNode();
        }

        JsonNode authConfigNode = objectMapper.valueToTree(authConfig);
        if (!authConfigNode.isObject()) {
            throw new IllegalArgumentException("authConfig must be a valid JSON object.");
        }
        return authConfigNode;
    }
}
