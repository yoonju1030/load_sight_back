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

import java.util.List;
import java.util.Objects;
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
    public double getStatistics(String runId) {
        List<StatisticDto> runResult = runQueryMapper.getRunStatistics(runId);
        int total = runResult.stream()
                .filter(Objects::nonNull)
                .mapToInt(dto -> dto.getCount())
                .sum();
        int success = runResult.stream().filter(obj -> obj.getStatus().equals(true))
                .collect(Collectors.toList())
                .get(0).getCount();
        double successRate = (double)success / (double) total;
        return successRate;
    }

}
