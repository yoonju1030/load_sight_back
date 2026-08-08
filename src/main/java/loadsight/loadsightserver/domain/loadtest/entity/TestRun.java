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

    public TestRun(TestPlan testPlan, AppUser owner, JsonNode configSnapshot) {
        this.testPlan = testPlan;
        this.owner = owner;
        this.status = RunStatus.CREATED;
        this.configSnapshot = configSnapshot;
    }
}
