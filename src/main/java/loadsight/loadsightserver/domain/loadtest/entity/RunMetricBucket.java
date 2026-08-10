package loadsight.loadsightserver.domain.loadtest.entity;

import jakarta.persistence.*;
import loadsight.loadsightserver.domain.common.CreatedAtEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "run_metric_bucket",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_run_metric_bucket",
                columnNames = {"run_id", "bucket_at"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunMetricBucket extends CreatedAtEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "run_id", nullable = false)
        private TestRun run;

        @Column(name = "bucket_at", nullable = false)
        private OffsetDateTime bucketAt;

        @Column(name = "request_count", nullable = false)
        private long requestCount;

        @Column(name = "success_count", nullable = false)
        private long successCount;

        @Column(name = "error_count", nullable = false)
        private long errorCount;

        @Column(name = "actual_rps", nullable = false, precision = 12, scale = 3)
        private BigDecimal actualRps;

        @Column(name = "avg_response_ms", precision = 12, scale = 3)
        private BigDecimal avgResponseMs;

        @Column(name = "min_response_ms")
        private Long minResponseMs;

        @Column(name = "max_response_ms")
        private Long maxResponseMs;

        @Column(name = "p50_ms", precision = 12, scale = 3)
        private BigDecimal p50Ms;

        @Column(name = "p95_ms", precision = 12, scale = 3)
        private BigDecimal p95Ms;

        @Column(name = "p99_ms", precision = 12, scale = 3)
        private BigDecimal p99Ms;

        @Builder
        private RunMetricBucket(TestRun run, OffsetDateTime bucketAt, long requestCount,
                                long successCount, long errorCount, BigDecimal actualRps,
                                BigDecimal avgResponseMs, Long minResponseMs, Long maxResponseMs,
                                BigDecimal p50Ms, BigDecimal p95Ms, BigDecimal p99Ms) {
                this.run = run;
                this.bucketAt = bucketAt;
                this.requestCount = requestCount;
                this.successCount = successCount;
                this.errorCount = errorCount;
                this.actualRps = actualRps;
                this.avgResponseMs = avgResponseMs;
                this.minResponseMs = minResponseMs;
                this.maxResponseMs = maxResponseMs;
                this.p50Ms = p50Ms;
                this.p95Ms = p95Ms;
                this.p99Ms = p99Ms;
        }
}
