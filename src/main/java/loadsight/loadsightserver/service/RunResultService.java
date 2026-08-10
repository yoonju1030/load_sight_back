package loadsight.loadsightserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import loadsight.loadsightserver.domain.loadtest.entity.RunAnalysisItem;
import loadsight.loadsightserver.domain.loadtest.entity.RunErrorSample;
import loadsight.loadsightserver.domain.loadtest.entity.RunMetricBucket;
import loadsight.loadsightserver.domain.loadtest.entity.RunResultBreakdown;
import loadsight.loadsightserver.domain.loadtest.entity.RunSlowSample;
import loadsight.loadsightserver.domain.loadtest.entity.TestRun;
import loadsight.loadsightserver.domain.loadtest.enums.AnalysisSeverity;
import loadsight.loadsightserver.domain.loadtest.enums.RunErrorType;
import loadsight.loadsightserver.domain.loadtest.enums.RunStatus;
import loadsight.loadsightserver.dto.GeneratorRunResultRequest;
import loadsight.loadsightserver.repository.RunResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

@Service
public class RunResultService {

    private final RunResultRepository repository;
    private final ObjectMapper objectMapper;

    public RunResultService(RunResultRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void store(UUID runId, GeneratorRunResultRequest request) {
        TestRun run = repository.findRunWithPlan(runId)
                .orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));

        repository.clearResult(runId);
        saveMetricBuckets(run, request);
        saveBreakdowns(run, request);
        saveErrorSamples(run, request);
        saveSlowSamples(run, request);
        createAnalysisItems(run, request.summary());

        GeneratorRunResultRequest.Summary summary = request.summary();
        run.applyResult(
                finalStatus(request.status()),
                request.startedAt(),
                request.endedAt(),
                summary.totalRequests(),
                summary.successCount(),
                summary.errorCount(),
                decimal(summary.p95LatencyMs()),
                decimal(summary.errorRate()),
                request.error()
        );
    }

    private void saveMetricBuckets(TestRun run, GeneratorRunResultRequest request) {
        request.metricBuckets().forEach(bucket -> repository.save(
                RunMetricBucket.builder()
                        .run(run)
                        .bucketAt(bucket.bucketAt())
                        .requestCount(bucket.requestCount())
                        .successCount(bucket.successCount())
                        .errorCount(bucket.errorCount())
                        .actualRps(decimal(bucket.actualRps()))
                        .avgResponseMs(decimal(bucket.averageResponseMs()))
                        .minResponseMs(bucket.minResponseMs())
                        .maxResponseMs(bucket.maxResponseMs())
                        .p50Ms(decimal(bucket.p50Ms()))
                        .p95Ms(decimal(bucket.p95Ms()))
                        .p99Ms(decimal(bucket.p99Ms()))
                        .build()
        ));
    }

    private void saveBreakdowns(TestRun run, GeneratorRunResultRequest request) {
        request.resultBreakdowns().forEach(item -> repository.save(
                RunResultBreakdown.builder()
                        .run(run)
                        .resultKey(item.resultKey())
                        .httpStatus(item.httpStatus())
                        .resultCount(item.resultCount())
                        .build()
        ));
    }

    private void saveErrorSamples(TestRun run, GeneratorRunResultRequest request) {
        request.errorSamples().forEach(sample -> repository.save(
                RunErrorSample.builder()
                        .run(run)
                        .occurredAt(sample.occurredAt())
                        .errorType(errorType(sample.errorType()))
                        .httpStatus(sample.httpStatus())
                        .latencyMs(sample.latencyMs())
                        .requestId(sample.requestId())
                        .requestUrl(sample.requestUrl())
                        .message(sample.message())
                        .responsePreview(sample.responsePreview())
                        .build()
        ));
    }

    private void saveSlowSamples(TestRun run, GeneratorRunResultRequest request) {
        request.slowSamples().forEach(sample -> repository.save(
                RunSlowSample.builder()
                        .run(run)
                        .occurredAt(sample.occurredAt())
                        .latencyMs(sample.latencyMs())
                        .httpStatus(sample.httpStatus())
                        .requestId(sample.requestId())
                        .requestUrl(sample.requestUrl())
                        .responsePreview(sample.responsePreview())
                        .build()
        ));
    }

    private void createAnalysisItems(TestRun run, GeneratorRunResultRequest.Summary summary) {
        double maxErrorRate = run.getConfigSnapshot().path("maxErrorRate").asDouble(1.0);
        double maxP95Ms = run.getConfigSnapshot().path("maxP95Ms").asDouble(1000);
        double targetRps = run.getConfigSnapshot().path("targetRps").asDouble(10);
        int order = 0;

        if (summary.totalRequests() == 0) {
            repository.save(analysis(
                    run,
                    "NO_REQUESTS",
                    AnalysisSeverity.CRITICAL,
                    "No requests completed",
                    "The generator did not complete any target requests.",
                    evidence("totalRequests", 0),
                    order++
            ));
        }
        if (summary.errorRate() > maxErrorRate) {
            ObjectNode evidence = objectMapper.createObjectNode();
            evidence.put("actualErrorRate", summary.errorRate());
            evidence.put("threshold", maxErrorRate);
            repository.save(analysis(
                    run,
                    "ERROR_RATE_EXCEEDED",
                    AnalysisSeverity.CRITICAL,
                    "Error rate exceeded",
                    "The observed error rate exceeded the configured threshold.",
                    evidence,
                    order++
            ));
        }
        if (summary.p95LatencyMs() > maxP95Ms) {
            ObjectNode evidence = objectMapper.createObjectNode();
            evidence.put("actualP95Ms", summary.p95LatencyMs());
            evidence.put("thresholdMs", maxP95Ms);
            repository.save(analysis(
                    run,
                    "P95_LATENCY_EXCEEDED",
                    AnalysisSeverity.WARNING,
                    "P95 latency exceeded",
                    "The observed p95 latency exceeded the configured threshold.",
                    evidence,
                    order++
            ));
        }
        if (targetRps > 0 && summary.averageRps() < targetRps * 0.9) {
            ObjectNode evidence = objectMapper.createObjectNode();
            evidence.put("actualRps", summary.averageRps());
            evidence.put("targetRps", targetRps);
            repository.save(analysis(
                    run,
                    "TARGET_RPS_NOT_REACHED",
                    AnalysisSeverity.WARNING,
                    "Target RPS was not reached",
                    "Average throughput remained below 90% of the configured target.",
                    evidence,
                    order++
            ));
        }
        if (order == 0) {
            repository.save(analysis(
                    run,
                    "WITHIN_THRESHOLDS",
                    AnalysisSeverity.INFO,
                    "Run stayed within thresholds",
                    "No configured performance threshold was exceeded.",
                    objectMapper.createObjectNode(),
                    0
            ));
        }
    }

    private RunAnalysisItem analysis(
            TestRun run,
            String ruleCode,
            AnalysisSeverity severity,
            String title,
            String message,
            ObjectNode evidence,
            int order
    ) {
        return RunAnalysisItem.builder()
                .run(run)
                .ruleCode(ruleCode)
                .severity(severity)
                .title(title)
                .message(message)
                .evidence(evidence)
                .displayOrder(order)
                .build();
    }

    private ObjectNode evidence(String key, long value) {
        ObjectNode evidence = objectMapper.createObjectNode();
        evidence.put(key, value);
        return evidence;
    }

    private RunStatus finalStatus(String status) {
        return switch (status.toUpperCase(Locale.ROOT)) {
            case "COMPLETED" -> RunStatus.COMPLETED;
            case "CANCELLED", "CANCELED", "STOPPED" -> RunStatus.CANCELLED;
            case "FAILED" -> RunStatus.FAILED;
            default -> throw new IllegalArgumentException("Unsupported final run status: " + status);
        };
    }

    private RunErrorType errorType(String value) {
        try {
            return RunErrorType.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return RunErrorType.UNKNOWN;
        }
    }

    private BigDecimal decimal(double value) {
        return BigDecimal.valueOf(value);
    }
}
