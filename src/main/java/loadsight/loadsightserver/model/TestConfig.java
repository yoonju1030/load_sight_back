package loadsight.loadsightserver.model;

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
public class TestConfig {
    private String id;
    private String name;
    private String targetUrl;
    private String method; // GET, POST, PUT, DELETE 등
    private Map<String, String> headers;
    private String body; // JSON body for POST/PUT
    private int threads; // 동시 스레드 수
    private int duration; // 테스트 지속 시간 (초)
    private int rampUp; // 램프업 시간 (초)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

