package loadsight.loadsightserver.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.OffsetDateTime;
import java.util.List;

public record GeneratorRunResultRequest(
        @NotBlank String status,
        OffsetDateTime startedAt,
        OffsetDateTime endedAt,
        @Valid @NotNull Summary summary,
        @Valid @NotNull List<MetricBucket> metricBuckets,
        @Valid @NotNull List<ResultBreakdown> resultBreakdowns,
        @Valid @NotNull List<ErrorSample> errorSamples,
        @Valid @NotNull List<SlowSample> slowSamples,
        String error
) {
    public record Summary(
            @PositiveOrZero long totalRequests,
            @PositiveOrZero long successCount,
            @PositiveOrZero long errorCount,
            @PositiveOrZero double errorRate,
            @PositiveOrZero double p95LatencyMs,
            @PositiveOrZero double averageRps
    ) {
    }

    public record MetricBucket(
            @NotNull OffsetDateTime bucketAt,
            @PositiveOrZero long requestCount,
            @PositiveOrZero long successCount,
            @PositiveOrZero long errorCount,
            @PositiveOrZero double actualRps,
            @PositiveOrZero double averageResponseMs,
            @PositiveOrZero long minResponseMs,
            @PositiveOrZero long maxResponseMs,
            @PositiveOrZero double p50Ms,
            @PositiveOrZero double p95Ms,
            @PositiveOrZero double p99Ms
    ) {
    }

    public record ResultBreakdown(
            @NotBlank String resultKey,
            Integer httpStatus,
            @PositiveOrZero long resultCount
    ) {
    }

    public record ErrorSample(
            @NotNull OffsetDateTime occurredAt,
            @NotBlank String errorType,
            Integer httpStatus,
            @PositiveOrZero Long latencyMs,
            String requestId,
            @NotBlank String requestUrl,
            String message,
            String responsePreview
    ) {
    }

    public record SlowSample(
            @NotNull OffsetDateTime occurredAt,
            @PositiveOrZero long latencyMs,
            Integer httpStatus,
            String requestId,
            @NotBlank String requestUrl,
            String responsePreview
    ) {
    }
}
