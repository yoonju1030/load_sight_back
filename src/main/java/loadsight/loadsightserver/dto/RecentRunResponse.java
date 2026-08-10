package loadsight.loadsightserver.dto;

import loadsight.loadsightserver.domain.loadtest.entity.TestRun;
import loadsight.loadsightserver.domain.loadtest.enums.RunStatus;

import java.time.OffsetDateTime;
import java.time.Duration;
import java.util.UUID;

public record RecentRunResponse(
        UUID id,
        UUID planId,
        String planName,
        RunStatus status,
        OffsetDateTime startTime,
        Long duration,
        Long totalRequests,
        Double successRate,
        Long p95Latency,
        Double errorRate
) {
    public static RecentRunResponse from(TestRun run) {
        return new RecentRunResponse(
                run.getId(),
                run.getTestPlan().getId(),
                run.getTestPlan().getName(),
                run.getStatus(),
                run.getStartedAt() == null ? run.getCreatedAt() : run.getStartedAt(),
                duration(run),
                run.getTotalRequests(),
                successRate(run),
                run.getP95LatencyMs() == null ? null : Math.round(run.getP95LatencyMs().doubleValue()),
                run.getErrorRate() == null ? null : run.getErrorRate().doubleValue()
        );
    }

    private static Long duration(TestRun run) {
        if (run.getStartedAt() == null || run.getEndedAt() == null) {
            return null;
        }
        return Duration.between(run.getStartedAt(), run.getEndedAt()).getSeconds();
    }

    private static Double successRate(TestRun run) {
        if (run.getTotalRequests() == null || run.getTotalRequests() == 0 || run.getSuccessCount() == null) {
            return null;
        }
        return run.getSuccessCount() * 100.0 / run.getTotalRequests();
    }
}
