package loadsight.loadsightserver.service;

import loadsight.loadsightserver.domain.RunEntity;
import loadsight.loadsightserver.dto.ExistedRunRequest;
import loadsight.loadsightserver.dto.RunRequest;
import loadsight.loadsightserver.repository.RunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RunService {

    @Autowired
    RunRepository runRepository;

    @Transactional
    public RunEntity startRun(RunRequest request) {
        int testId = request.getTestId();
        RunEntity createdRun = runRepository.save(testId);
        return createdRun;
    }

    @Transactional
    public void stopRun(ExistedRunRequest request) {
        int runId = request.getRunId();
        runRepository.stop(runId);
    }
}
