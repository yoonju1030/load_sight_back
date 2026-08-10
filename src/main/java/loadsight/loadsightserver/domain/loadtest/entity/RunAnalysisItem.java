package loadsight.loadsightserver.domain.loadtest.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import jakarta.persistence.*;
import loadsight.loadsightserver.domain.loadtest.enums.AnalysisSeverity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Entity
@Table(
        name = "run_analysis_item",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_run_analysis_rule", columnNames = {"run_id", "rule_code"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunAnalysisItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "run_id", nullable = false)
    private TestRun run;

    @Column(name = "rule_code", nullable = false, length = 80)
    private String ruleCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private AnalysisSeverity severity;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "text")
    private String message;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "evidence", nullable = false, columnDefinition = "jsonb")
    private JsonNode evidence = JsonNodeFactory.instance.objectNode();

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Builder
    private RunAnalysisItem(TestRun run, String ruleCode, AnalysisSeverity severity,
                            String title, String message, JsonNode evidence, Integer displayOrder) {
        this.run = run;
        this.ruleCode = ruleCode;
        this.severity = severity;
        this.title = title;
        this.message = message;
        this.evidence = evidence == null ? JsonNodeFactory.instance.objectNode() : evidence;
        this.displayOrder = displayOrder == null ? 0 : displayOrder;
    }
}
