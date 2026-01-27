package loadsight.loadsightserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestRequest {
    @NotBlank(message = "테스트 이름은 필수입니다")
    private String name;
    
    @NotBlank(message = "대상 URL은 필수입니다")
    @Pattern(regexp = "^https?://.*", message = "유효한 HTTP/HTTPS URL이어야 합니다")
    private String targetUrl;
    
    @NotBlank(message = "HTTP 메서드는 필수입니다")
    @Pattern(regexp = "GET|POST|PUT|DELETE|PATCH", message = "유효한 HTTP 메서드여야 합니다")
    private String method;
    
    private Map<String, String> headers;
    private String body;
    
    @Min(value = 1, message = "스레드 수는 최소 1개 이상이어야 합니다")
    @Max(value = 1000, message = "스레드 수는 최대 1000개까지 가능합니다")
    private int threads;
    
    @Min(value = 1, message = "테스트 지속 시간은 최소 1초 이상이어야 합니다")
    @Max(value = 3600, message = "테스트 지속 시간은 최대 3600초까지 가능합니다")
    private int duration;
    
    @Min(value = 0, message = "램프업 시간은 0 이상이어야 합니다")
    private int rampUp;
}

