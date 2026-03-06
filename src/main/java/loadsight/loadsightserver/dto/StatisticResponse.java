package loadsight.loadsightserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticResponse {
    private double successRate;
    private double failRate;
    private int success;
    private int fail;
    private int totalRequests;
    private double errorRate;
}
