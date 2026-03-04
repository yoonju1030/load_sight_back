package loadsight.loadsightserver.service;

import loadsight.loadsightserver.domain.RunEntity;
import loadsight.loadsightserver.dto.ExistedRunRequest;
import loadsight.loadsightserver.dto.RunRequest;
import loadsight.loadsightserver.dto.StatisticDto;
import loadsight.loadsightserver.mybatis.RunQueryMapper;
import loadsight.loadsightserver.repository.RunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RunService {

    @Autowired
    RunRepository runRepository;
    @Autowired
    RunQueryMapper runQueryMapper;

    @Transactional
    public RunEntity startRun(RunRequest request) {
        int testId = request.getTestId();
//        RunEntity createdRun = runRepository.save(testId);
        RunEntity createdRun = new RunEntity();
        return createdRun;
    }

    @Transactional
    public void stopRun(ExistedRunRequest request) {
        int runId = request.getRunId();
        runRepository.stop(runId);
    }

    @Transactional(readOnly = true)
    public StatisticDto getStatistics(String runId) {
        StatisticDto runResult = runQueryMapper.getRunStatistics(runId);
        return runResult;
    }

}
