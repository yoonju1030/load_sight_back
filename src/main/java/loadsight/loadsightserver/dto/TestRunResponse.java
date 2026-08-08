package loadsight.loadsightserver.dto;

import loadsight.loadsightserver.domain.loadtest.entity.TestRun;
import loadsight.loadsightserver.domain.loadtest.enums.RunStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TestRunResponse(
        UUID runId,
        UUID planId,
        RunStatus status,
        OffsetDateTime createdAt
) {
    public static TestRunResponse from(TestRun run) {
        return new TestRunResponse(
                run.getId(),
                run.getTestPlan().getId(),
                run.getStatus(),
                run.getCreatedAt()
        );
    }
}
