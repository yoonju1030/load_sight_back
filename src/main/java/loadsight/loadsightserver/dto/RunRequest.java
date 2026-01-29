package loadsight.loadsightserver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunRequest {
    @NotNull(message = "테스트 id는 필수입니다")
    private Integer testId;
}
