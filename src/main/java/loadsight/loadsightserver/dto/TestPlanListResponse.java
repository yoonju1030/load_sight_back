package loadsight.loadsightserver.dto;

import loadsight.loadsightserver.domain.loadtest.entity.TestPlan;
import loadsight.loadsightserver.domain.loadtest.enums.HttpMethodType;
import loadsight.loadsightserver.domain.loadtest.enums.RunStatus;

import java.time.LocalDate;
import java.util.UUID;

public record TestPlanListResponse(
        UUID id,
        String name,
        String description,
        HttpMethodType method,
        String url,
        int rps,
        int duration,
        int concurrency,
        String lastRunStatus,
        String statusTone,
        LocalDate updatedAt
) {
    public static TestPlanListResponse from(TestPlan plan, RunStatus lastRunStatus) {
        return new TestPlanListResponse(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getHttpMethod(),
                plan.getTargetUrl(),
                plan.getTargetRps(),
                plan.getDurationSeconds(),
                plan.getConcurrency(),
                lastRunStatus == null ? null : lastRunStatus.name(),
                statusTone(lastRunStatus),
                plan.getUpdatedAt() == null ? null : plan.getUpdatedAt().toLocalDate()
        );
    }

    private static String statusTone(RunStatus status) {
        if (status == RunStatus.COMPLETED) {
            return "success";
        }
        if (status == RunStatus.FAILED) {
            return "danger";
        }
        return "neutral";
    }
}
