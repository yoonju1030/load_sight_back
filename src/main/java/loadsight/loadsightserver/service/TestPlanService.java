package loadsight.loadsightserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import loadsight.loadsightserver.domain.auth.entity.AppUser;
import loadsight.loadsightserver.domain.loadtest.entity.TestPlan;
import loadsight.loadsightserver.domain.loadtest.entity.TestRun;
import loadsight.loadsightserver.domain.loadtest.enums.HttpMethodType;
import loadsight.loadsightserver.domain.loadtest.enums.RunStatus;
import loadsight.loadsightserver.dto.PageResponse;
import loadsight.loadsightserver.dto.TestPlanCreateRequest;
import loadsight.loadsightserver.dto.TestPlanCreateResponse;
import loadsight.loadsightserver.dto.TestPlanListResponse;
import loadsight.loadsightserver.dto.TestRunResponse;
import loadsight.loadsightserver.exception.TestPlanNotFoundException;
import loadsight.loadsightserver.event.TestRunCreatedEvent;
import loadsight.loadsightserver.repository.TestPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TestPlanService {

    @Autowired
    TestPlanRepository testPlanRepository;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public PageResponse<TestPlanListResponse> getAll(
            UUID userId,
            int page,
            int size,
            String search,
            String method
    ) {
        String normalizedSearch = normalizeFilter(search);
        HttpMethodType methodFilter = parseMethod(method);
        List<TestPlan> plans = testPlanRepository.findPageByOwner(
                userId, normalizedSearch, methodFilter, page, size
        );
        Map<UUID, RunStatus> latestStatuses = testPlanRepository.findLatestRunStatuses(
                plans.stream().map(TestPlan::getId).toList()
        );
        List<TestPlanListResponse> content = plans.stream()
                .map(plan -> TestPlanListResponse.from(plan, latestStatuses.get(plan.getId())))
                .toList();
        long totalElements = testPlanRepository.countByOwner(userId, normalizedSearch, methodFilter);

        return PageResponse.of(content, page, size, totalElements);
    }

    private String normalizeFilter(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private HttpMethodType parseMethod(String value) {
        String normalized = normalizeFilter(value);
        if (normalized == null || normalized.equalsIgnoreCase("ALL")) {
            return null;
        }
        try {
            return HttpMethodType.valueOf(normalized.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unsupported HTTP method: " + value);
        }
    }

    @Transactional
    public TestPlanCreateResponse create(TestPlanCreateRequest request, UUID userId) {
        AppUser user = testPlanRepository.findUserById(userId)
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

        return TestPlanCreateResponse.from(testPlanRepository.saveTestPlan(testPlan));
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

    @Transactional
    public TestRunResponse createRun(UUID planId, UUID userId) {
        TestPlan plan = testPlanRepository.findActivePlanByIdAndOwner(planId, userId)
                .orElseThrow(TestPlanNotFoundException::new);

        TestRun run = testPlanRepository.saveTestRun(new TestRun(plan, plan.getOwner(), createSnapshot(plan)));
        eventPublisher.publishEvent(new TestRunCreatedEvent(run.getId()));
        return TestRunResponse.from(run);
    }

    private JsonNode createSnapshot(TestPlan plan) {
        ObjectNode snapshot = JsonNodeFactory.instance.objectNode();
        snapshot.put("name", plan.getName());
        snapshot.put("description", plan.getDescription());
        snapshot.put("method", plan.getHttpMethod().name());
        snapshot.put("url", plan.getTargetUrl());
        snapshot.set("headers", toJsonObject(plan.getRequestHeaders()));
        snapshot.put("requestBody", plan.getRequestBody());
        snapshot.put("authentication", plan.getAuthType().name());
        snapshot.set("authConfig", plan.getAuthConfigEncrypted().deepCopy());
        snapshot.put("timeoutMs", plan.getTimeoutMs());
        snapshot.put("targetRps", plan.getTargetRps());
        snapshot.put("durationSec", plan.getDurationSeconds());
        snapshot.put("concurrency", plan.getConcurrency());
        snapshot.put("maxErrorRate", plan.getMaxErrorRate());
        snapshot.put("maxP95Ms", plan.getMaxP95Ms());
        return snapshot;
    }

    private ObjectNode toJsonObject(Map<String, Object> values) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        values.forEach((key, value) -> node.set(key, JsonNodeFactory.instance.pojoNode(value)));
        return node;
    }
}
