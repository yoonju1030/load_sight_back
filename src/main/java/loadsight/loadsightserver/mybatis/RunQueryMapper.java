package loadsight.loadsightserver.mybatis;

import loadsight.loadsightserver.dto.StatisticDto;
import org.apache.ibatis.annotations.Param;

public interface RunQueryMapper {
    StatisticDto getRunStatistics(@Param("runId") String runId);
}
