package loadsight.loadsightserver.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(
        name = "runs",
        indexes = {
                @Index(name="idx_runs_test_id", columnList = "test_id"),
                @Index(name="idx_runs_status", columnList = "runStatus"),
                @Index(name="idx_runs_started_at", columnList = "startedAt"),
                @Index(name="idx_runs_created_at", columnList = "createdAt")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RunEntity extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="test_id", nullable = false)
    private TestEntity test;

    @Column(length=120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RunStatus runStatus;

    @Column
    private OffsetDateTime startedAt;

    @Column
    private OffsetDateTime endedAt;

    // 실행 당시 스냅샷
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", nullable = false)
    private Map<String, Object> specSnapshotJson;

    // 종료 후 요약 리포트
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", nullable = false)
    private Map<String, Object> reportJson;

    @Column(length=500)
    private String stopReason;

    @Column(length=500)
    private String failReason;


}
