package loadsight.loadsightserver.dto;

import jakarta.security.auth.message.AuthStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestRequest {
    @NotBlank(message = "테스트 이름은 필수입니다")
    private String name;

    private String descriptions;

    @NotBlank(message = "대상 URL은 필수입니다")
    @Pattern(regexp = "^https?://.*", message = "유효한 HTTP/HTTPS URL이어야 합니다")
    private String targetUrl;

    @NotBlank(message = "HTTP 메서드는 필수입니다")
    @Pattern(regexp = "GET|POST|PUT|DELETE|PATCH", message = "유효한 HTTP 메서드여야 합니다")
    private String method;

    private Map<String, Object> data;
    private Map<String, String> headers;
    private String body;

    @JsonProperty("thread")
    @NotNull(message = "스레드 수는 필수입니다")
    @Min(value = 1, message = "스레드 수는 최소 1개 이상이어야 합니다")
    @Max(value = 1000, message = "스레드 수는 최대 1000개까지 가능합니다")
    private Integer threads;

    @NotNull(message = "총 요청수는 필수입니다")
    @Min(value = 1, message = "총 요청수는 최소 1회 이상이어야 합니다")
    @Max(value = 1_000_000, message = "총 요청수는 최대 100만 회까지 가능합니다")
    private Integer totalRequest;

    @NotBlank(message = "auth 방식은 필수입니다.")
    private AuthStatus authType;

    private Map<String, Object> auth;


    @Min(value = 0, message = "요청 간격은 0ms 이상이어야 합니다")
    private Integer requestInterval;
}

