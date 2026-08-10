package loadsight.loadsightserver.domain.loadtest.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(
        name = "run_result_breakdown",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_run_result_breakdown", columnNames = {"run_id", "result_key"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunResultBreakdown {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "run_id", nullable = false)
    private TestRun run;

    @Column(name = "result_key", nullable = false, length = 40)
    private String resultKey;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "result_count", nullable = false)
    private long resultCount;

    @Builder
    private RunResultBreakdown(TestRun run, String resultKey, Integer httpStatus, long resultCount) {
        this.run = run;
        this.resultKey = resultKey;
        this.httpStatus = httpStatus;
        this.resultCount = resultCount;
    }
}
