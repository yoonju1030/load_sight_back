package loadsight.loadsightserver.domain.loadtest.entity;

import jakarta.persistence.*;
import loadsight.loadsightserver.domain.loadtest.enums.RunErrorType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "run_error_sample")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunErrorSample {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "run_id", nullable = false)
    private TestRun run;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "error_type", nullable = false, length = 30)
    private RunErrorType errorType;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "latency_ms")
    private Long latencyMs;

    @Column(name = "request_id", length = 120)
    private String requestId;

    @Column(name = "request_url", nullable = false, columnDefinition = "text")
    private String requestUrl;

    @Column(name = "message", length = 1000)
    private String message;

    @Column(name = "response_preview", columnDefinition = "text")
    private String responsePreview;

    @Builder
    private RunErrorSample(TestRun run, OffsetDateTime occurredAt, RunErrorType errorType,
                           Integer httpStatus, Long latencyMs, String requestId,
                           String requestUrl, String message, String responsePreview) {
        this.run = run;
        this.occurredAt = occurredAt;
        this.errorType = errorType;
        this.httpStatus = httpStatus;
        this.latencyMs = latencyMs;
        this.requestId = requestId;
        this.requestUrl = requestUrl;
        this.message = message;
        this.responsePreview = responsePreview;
    }
}
