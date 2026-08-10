package loadsight.loadsightserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public record GeneratorRunRequest(
        @JsonProperty("run_id") String runId,
        @JsonProperty("target_url") String targetUrl,
        @JsonProperty("duration_s") double durationSeconds,
        double rps,
        int concurrency,
        @JsonProperty("slow_threshold_ms") int slowThresholdMs,
        @JsonProperty("max_error_samples") int maxErrorSamples,
        @JsonProperty("max_slow_samples") int maxSlowSamples,
        Scenario scenario,
        @JsonProperty("callback_url") String callbackUrl,
        @JsonProperty("callback_token") String callbackToken
) {
    public record Scenario(
            String method,
            Map<String, String> headers,
            @JsonProperty("query_params") Map<String, String> queryParams,
            JsonNode json,
            String content,
            @JsonProperty("timeout_s") double timeoutSeconds
    ) {
    }
}
