package loadsight.loadsightserver.domain.loadtest.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import jakarta.persistence.*;
import loadsight.loadsightserver.domain.auth.entity.AppUser;
import loadsight.loadsightserver.domain.common.BaseTimeEntity;
import loadsight.loadsightserver.domain.loadtest.enums.AuthType;
import loadsight.loadsightserver.domain.loadtest.enums.HttpMethodType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Entity
@Table(name = "test_plan")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestPlan extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private AppUser owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private AppUser createdBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "updated_by_user_id", nullable = false)
    private AppUser updatedBy;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "http_method", nullable = false, length = 10)
    private HttpMethodType httpMethod;

    @Column(name = "target_url", nullable = false, columnDefinition = "text")
    private String targetUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_headers", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> requestHeaders = new LinkedHashMap<>();

    @Column(name = "request_body", columnDefinition = "text")
    private String requestBody;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", nullable = false, length = 30)
    private AuthType authType = AuthType.NONE;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "auth_config_encrypted", nullable = false, columnDefinition = "jsonb")
    private JsonNode authConfigEncrypted = JsonNodeFactory.instance.objectNode();

    @Column(name = "timeout_ms", nullable = false)
    private int timeoutMs = 5000;

    @Column(name = "target_rps", nullable = false)
    private int targetRps = 10;

    @Column(name = "duration_seconds", nullable = false)
    private int durationSeconds = 30;

    @Column(name = "concurrency", nullable = false)
    private int concurrency = 10;

    @Column(name = "max_error_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxErrorRate = new BigDecimal("1.00");

    @Column(name = "max_p95_ms", nullable = false)
    private int maxP95Ms = 1000;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Builder
    private TestPlan(AppUser owner, AppUser createdBy, String name, String description,
                     HttpMethodType httpMethod, String targetUrl, Map<String, Object> requestHeaders,
                     String requestBody, AuthType authType, JsonNode authConfigEncrypted,
                     Integer timeoutMs, Integer targetRps, Integer durationSeconds,
                     Integer concurrency, BigDecimal maxErrorRate, Integer maxP95Ms) {
        this.owner = owner;
        this.createdBy = createdBy;
        this.updatedBy = createdBy;
        this.name = name;
        this.description = description;
        this.httpMethod = httpMethod;
        this.targetUrl = targetUrl;
        this.requestHeaders = requestHeaders == null ? new LinkedHashMap<>() : new LinkedHashMap<>(requestHeaders);
        this.requestBody = requestBody;
        this.authType = authType == null ? AuthType.NONE : authType;
        this.authConfigEncrypted = authConfigEncrypted == null ? JsonNodeFactory.instance.objectNode() : authConfigEncrypted;
        this.timeoutMs = timeoutMs == null ? 5000 : timeoutMs;
        this.targetRps = targetRps == null ? 10 : targetRps;
        this.durationSeconds = durationSeconds == null ? 30 : durationSeconds;
        this.concurrency = concurrency == null ? 10 : concurrency;
        this.maxErrorRate = maxErrorRate == null ? new BigDecimal("1.00") : maxErrorRate;
        this.maxP95Ms = maxP95Ms == null ? 1000 : maxP95Ms;
    }

    public void update(AppUser editor, String name, String description, HttpMethodType httpMethod,
                       String targetUrl, Map<String, Object> requestHeaders, String requestBody,
                       AuthType authType, JsonNode authConfigEncrypted, int timeoutMs,
                       int targetRps, int durationSeconds, int concurrency,
                       BigDecimal maxErrorRate, int maxP95Ms) {
        this.updatedBy = editor;
        this.name = name;
        this.description = description;
        this.httpMethod = httpMethod;
        this.targetUrl = targetUrl;
        this.requestHeaders = requestHeaders == null ? new LinkedHashMap<>() : new LinkedHashMap<>(requestHeaders);
        this.requestBody = requestBody;
        this.authType = authType;
        this.authConfigEncrypted = authConfigEncrypted;
        this.timeoutMs = timeoutMs;
        this.targetRps = targetRps;
        this.durationSeconds = durationSeconds;
        this.concurrency = concurrency;
        this.maxErrorRate = maxErrorRate;
        this.maxP95Ms = maxP95Ms;
    }

    public void softDelete(AppUser editor, OffsetDateTime now) {
        this.updatedBy = editor;
        this.deletedAt = now;
    }

}
