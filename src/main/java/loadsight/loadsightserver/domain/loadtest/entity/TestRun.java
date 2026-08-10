package loadsight.loadsightserver.domain.loadtest.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import loadsight.loadsightserver.domain.auth.entity.AppUser;
import loadsight.loadsightserver.domain.common.BaseTimeEntity;
import loadsight.loadsightserver.domain.loadtest.enums.RunStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "test_run")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestRun extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_plan_id", nullable = false)
    private TestPlan testPlan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private AppUser owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RunStatus status = RunStatus.CREATED;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config_snapshot", nullable = false, columnDefinition = "jsonb")
    private JsonNode configSnapshot;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "ended_at")
    private OffsetDateTime endedAt;

    @Column(name = "total_requests")
    private Long totalRequests;

    @Column(name = "success_count")
    private Long successCount;

    @Column(name = "error_count")
    private Long errorCount;

    @Column(name = "p95_latency_ms", precision = 12, scale = 3)
    private BigDecimal p95LatencyMs;

    @Column(name = "error_rate", precision = 7, scale = 3)
    private BigDecimal errorRate;

    @Column(name = "failure_message", length = 1000)
    private String failureMessage;

    public TestRun(TestPlan testPlan, AppUser owner, JsonNode configSnapshot) {
        this.testPlan = testPlan;
        this.owner = owner;
        this.status = RunStatus.CREATED;
        this.configSnapshot = configSnapshot;
    }

    public void markStarting() {
        if (this.status == RunStatus.CREATED) {
            this.status = RunStatus.STARTING;
        }
    }

    public void markRunning(OffsetDateTime startedAt) {
        if (this.status == RunStatus.CREATED || this.status == RunStatus.STARTING) {
            this.status = RunStatus.RUNNING;
            this.startedAt = startedAt == null ? OffsetDateTime.now() : startedAt;
        }
    }

    public void markFailed(String message) {
        if (this.status != RunStatus.COMPLETED && this.status != RunStatus.CANCELLED) {
            this.status = RunStatus.FAILED;
            this.endedAt = OffsetDateTime.now();
            this.failureMessage = truncate(message, 1000);
        }
    }

    public void applyResult(
            RunStatus finalStatus,
            OffsetDateTime startedAt,
            OffsetDateTime endedAt,
            long totalRequests,
            long successCount,
            long errorCount,
            BigDecimal p95LatencyMs,
            BigDecimal errorRate,
            String failureMessage
    ) {
        this.status = finalStatus;
        this.startedAt = startedAt == null ? this.startedAt : startedAt;
        this.endedAt = endedAt == null ? OffsetDateTime.now() : endedAt;
        this.totalRequests = totalRequests;
        this.successCount = successCount;
        this.errorCount = errorCount;
        this.p95LatencyMs = p95LatencyMs;
        this.errorRate = errorRate;
        this.failureMessage = truncate(failureMessage, 1000);
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
