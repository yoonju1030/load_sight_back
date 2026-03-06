package loadsight.loadsightserver.mybatis;

import loadsight.loadsightserver.domain.RunStatus;
import loadsight.loadsightserver.dto.StatisticDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RunQueryMapper {
    List<StatisticDto> getRunStatistics(@Param("runId") String runId);
    int getErrorCount(@Param("runId") String runId);
    RunStatus getRunStatus(@Param("runId") String runId);
}
