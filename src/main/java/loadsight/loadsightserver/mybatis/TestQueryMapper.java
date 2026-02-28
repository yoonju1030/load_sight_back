package loadsight.loadsightserver.mybatis;

import loadsight.loadsightserver.dto.TestSummaryDto;
import org.apache.ibatis.annotations.Param;

public interface TestQueryMapper {
    TestSummaryDto findTestSummary(@Param("testId") Long testId);
}
