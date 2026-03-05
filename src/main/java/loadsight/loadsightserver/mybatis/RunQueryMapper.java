package loadsight.loadsightserver.mybatis;

import loadsight.loadsightserver.dto.StatisticDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RunQueryMapper {
    List<StatisticDto> getRunStatistics(@Param("runId") String runId);
}
