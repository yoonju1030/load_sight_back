package loadsight.loadsightserver.domain.loadtest.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "run_slow_sample")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunSlowSample {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "run_id", nullable = false)
    private TestRun run;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @Column(name = "latency_ms", nullable = false)
    private long latencyMs;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "request_id", length = 120)
    private String requestId;

    @Column(name = "request_url", nullable = false, columnDefinition = "text")
    private String requestUrl;

    @Column(name = "response_preview", columnDefinition = "text")
    private String responsePreview;

    @Builder
    private RunSlowSample(TestRun run, OffsetDateTime occurredAt, long latencyMs,
                          Integer httpStatus, String requestId, String requestUrl,
                          String responsePreview) {
        this.run = run;
        this.occurredAt = occurredAt;
        this.latencyMs = latencyMs;
        this.httpStatus = httpStatus;
        this.requestId = requestId;
        this.requestUrl = requestUrl;
        this.responsePreview = responsePreview;
    }
}
