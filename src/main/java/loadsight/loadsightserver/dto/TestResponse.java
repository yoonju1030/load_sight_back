package loadsight.loadsightserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResponse {
    private String id;
    private String name;
    private String targetUrl;
    private String method;
    private Map<String, String> headers;
    private String body;
    private int threads;
    private int duration;
    private int rampUp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

