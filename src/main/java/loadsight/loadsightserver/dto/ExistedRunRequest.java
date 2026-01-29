package loadsight.loadsightserver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExistedRunRequest {
    @NotNull(message = "실행 id는 필수입니다.")
    private Integer runId;
}
