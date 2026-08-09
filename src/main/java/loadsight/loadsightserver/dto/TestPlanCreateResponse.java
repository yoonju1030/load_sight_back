package loadsight.loadsightserver.dto;

import loadsight.loadsightserver.domain.loadtest.entity.TestPlan;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TestPlanCreateResponse(
        UUID id,
        String name,
        OffsetDateTime createdAt
) {
    public static TestPlanCreateResponse from(TestPlan plan) {
        return new TestPlanCreateResponse(plan.getId(), plan.getName(), plan.getCreatedAt());
    }
}
