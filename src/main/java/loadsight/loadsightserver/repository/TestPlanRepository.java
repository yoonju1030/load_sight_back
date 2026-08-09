package loadsight.loadsightserver.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import loadsight.loadsightserver.domain.auth.entity.AppUser;
import loadsight.loadsightserver.domain.loadtest.entity.TestPlan;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TestPlanRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<AppUser> findUserById(UUID userId) {
        return Optional.ofNullable(em.find(AppUser.class, userId));
    }

    public TestPlan save(TestPlan testPlan) {
        em.persist(testPlan);
        em.flush();
        return testPlan;
    }
}
