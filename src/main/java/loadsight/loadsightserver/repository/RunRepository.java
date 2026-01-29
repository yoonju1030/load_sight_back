package loadsight.loadsightserver.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import loadsight.loadsightserver.domain.RunEntity;
import loadsight.loadsightserver.domain.RunStatus;
import loadsight.loadsightserver.domain.TestEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
public class RunRepository {

    @PersistenceContext
    private EntityManager em;

    public RunEntity save(int testId) {
        TestEntity test = em.find(TestEntity.class, testId);
        RunEntity runEntity = new RunEntity();
        runEntity.setTest(test);
        runEntity.setRunStatus(RunStatus.CREATED);
        runEntity.setSpecSnapshotJson(test.getSpecJson());
        runEntity.setReportJson(test.getSpecJson());
        em.persist(runEntity);
        return runEntity;
    }

    public void stop(int runId) {
        RunEntity run = em.find(RunEntity.class, runId);
        if (run == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "run not found: " + runId);
        }
        run.setRunStatus(RunStatus.STOPPING);
    }

}
