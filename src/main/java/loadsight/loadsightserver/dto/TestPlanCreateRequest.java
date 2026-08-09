package loadsight.loadsightserver.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import loadsight.loadsightserver.domain.loadtest.enums.AuthType;
import loadsight.loadsightserver.domain.loadtest.enums.HttpMethodType;

import java.math.BigDecimal;

public record TestPlanCreateRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 500)
        String description,

        @NotNull
        HttpMethodType method,

        @NotBlank
        String url,

        Object headers,

        String requestBody,

        @NotNull
        AuthType authentication,

        Object authConfig,

        @Min(1)
        @Max(300_000)
        int timeoutMs,

        @Min(1)
        int targetRps,

        @Min(1)
        int durationSec,

        @Min(1)
        int concurrency,

        @NotNull
        @DecimalMin("0.00")
        @DecimalMax("100.00")
        BigDecimal maxErrorRate,

        @Min(1)
        int maxP95Ms
) {
    public TestPlanCreateRequest {
        name = name == null ? null : name.trim();
        description = description == null ? null : description.trim();
        url = url == null ? null : url.trim();
    }
}
