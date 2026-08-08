package loadsight.loadsightserver.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import loadsight.loadsightserver.domain.loadtest.entity.TestPlan;
import loadsight.loadsightserver.domain.loadtest.entity.TestRun;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TestPlanRunRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<TestPlan> findActivePlanByIdAndOwner(UUID planId, UUID ownerId) {
        return em.createQuery("""
                        select p from TestPlan p
                        join fetch p.owner
                        where p.id = :planId
                          and p.owner.id = :ownerId
                          and p.deletedAt is null
                        """, TestPlan.class)
                .setParameter("planId", planId)
                .setParameter("ownerId", ownerId)
                .getResultStream()
                .findFirst();
    }

    public TestRun save(TestRun run) {
        em.persist(run);
        em.flush();
        return run;
    }
}
