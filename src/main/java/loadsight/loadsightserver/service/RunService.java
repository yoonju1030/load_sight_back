package loadsight.loadsightserver.service;

import loadsight.loadsightserver.domain.RunEntity;
import loadsight.loadsightserver.domain.RunStatus;
import loadsight.loadsightserver.dto.ExistedRunRequest;
import loadsight.loadsightserver.dto.RunRequest;
import loadsight.loadsightserver.dto.StatisticDto;
import loadsight.loadsightserver.dto.StatisticResponse;
import loadsight.loadsightserver.mybatis.RunQueryMapper;
import loadsight.loadsightserver.repository.RunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    public StatisticResponse getStatistics(String runId) {
        List<StatisticDto> runResult = runQueryMapper.getRunStatistics(runId);
        StatisticResponse statisticResponse = new StatisticResponse();
        int total = runResult.stream()
                .filter(Objects::nonNull)
                .mapToInt(dto -> dto.getCount())
                .sum();
        int success = runResult.stream().filter(obj -> obj.getStatus().equals(true))
                .collect(Collectors.toList())
                .get(0).getCount();
        int fail = total - success;
        double successRate = (double)success / (double) total;
        double failRate = (double)fail / (double) total;
        statisticResponse.setTotalRequests(total);
        statisticResponse.setSuccessRate(successRate);
        statisticResponse.setFailRate(failRate);
        statisticResponse.setSuccess(success);
        statisticResponse.setFail(fail);
        int error = runQueryMapper.getErrorCount(runId);
        statisticResponse.setErrorRate((double)error / (double)total);
        return statisticResponse;
    }

    @Transactional(readOnly = true)
    public boolean checkRunStatus(String runId) {
        boolean result = true;
        RunStatus runStatus = runQueryMapper.getRunStatus(runId);
        Set<RunStatus> notAllowed = Set.of(RunStatus.CREATED, RunStatus.STARTING, RunStatus.RUNNING);
        if (notAllowed.contains(runStatus)) {
            result = false;
        }
        return result;
    }

}
