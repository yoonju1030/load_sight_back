package loadsight.loadsightserver.domain;

import jakarta.persistence.*;
import jakarta.security.auth.message.AuthStatus;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;

@Entity
@Table(
        name="tests",
        indexes={
                @Index(name="idx_tests_name", columnList = "name"),
                @Index(name="idx_tests_created_at", columnList = "createdAt")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TestEntity extends BaseEntity{

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length=1000)
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", nullable = false)
    private Map<String, Object> specJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> header;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> Body;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> auth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthStatus authType;

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

}
