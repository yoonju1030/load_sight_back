package loadsight.loadsightserver.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import loadsight.loadsightserver.domain.loadtest.entity.TestRun;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TestRunExecutionRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<TestRun> findById(UUID runId) {
        return Optional.ofNullable(em.find(TestRun.class, runId));
    }
}
