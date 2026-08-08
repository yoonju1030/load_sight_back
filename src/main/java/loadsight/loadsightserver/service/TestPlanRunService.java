package loadsight.loadsightserver.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import loadsight.loadsightserver.domain.loadtest.entity.TestPlan;
import loadsight.loadsightserver.domain.loadtest.entity.TestRun;
import loadsight.loadsightserver.dto.TestRunResponse;
import loadsight.loadsightserver.exception.TestPlanNotFoundException;
import loadsight.loadsightserver.repository.TestPlanRunRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class TestPlanRunService {

    private final TestPlanRunRepository repository;

    public TestPlanRunService(TestPlanRunRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TestRunResponse createRun(UUID planId, UUID userId) {
        TestPlan plan = repository.findActivePlanByIdAndOwner(planId, userId)
                .orElseThrow(TestPlanNotFoundException::new);

        TestRun run = repository.save(new TestRun(plan, plan.getOwner(), createSnapshot(plan)));
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
