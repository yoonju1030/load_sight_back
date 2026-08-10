package loadsight.loadsightserver.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import loadsight.loadsightserver.domain.auth.entity.AppUser;
import loadsight.loadsightserver.domain.loadtest.entity.TestPlan;
import loadsight.loadsightserver.domain.loadtest.entity.TestRun;
import loadsight.loadsightserver.domain.loadtest.enums.HttpMethodType;
import loadsight.loadsightserver.domain.loadtest.enums.RunStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class TestPlanRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<AppUser> findUserById(UUID userId) {
        return Optional.ofNullable(em.find(AppUser.class, userId));
    }

    public List<TestPlan> findPageByOwner(
            UUID ownerId,
            String search,
            HttpMethodType method,
            int page,
            int size
    ) {
        String filters = testPlanFilters(search, method);
        var query = em.createQuery(
                        "select p " + filters + " order by p.updatedAt desc",
                        TestPlan.class
                )
                .setParameter("ownerId", ownerId)
                .setFirstResult(page * size)
                .setMaxResults(size);

        setTestPlanFilterParameters(query, search, method);
        return query.getResultList();
    }

    public long countByOwner(UUID ownerId, String search, HttpMethodType method) {
        String filters = testPlanFilters(search, method);
        var query = em.createQuery("select count(p) " + filters, Long.class)
                .setParameter("ownerId", ownerId);

        setTestPlanFilterParameters(query, search, method);
        return query.getSingleResult();
    }

    public Map<UUID, RunStatus> findLatestRunStatuses(List<UUID> planIds) {
        if (planIds.isEmpty()) {
            return Map.of();
        }

        return em.createQuery("""
                        select r.testPlan.id, r.status
                        from TestRun r
                        where r.testPlan.id in :planIds
                          and r.createdAt = (
                              select max(latest.createdAt)
                              from TestRun latest
                              where latest.testPlan.id = r.testPlan.id
                          )
                        """, Object[].class)
                .setParameter("planIds", planIds)
                .getResultList()
                .stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> (RunStatus) row[1],
                        (first, ignored) -> first
                ));
    }

    private String testPlanFilters(String search, HttpMethodType method) {
        StringBuilder filters = new StringBuilder("""
                 from TestPlan p
                 where p.owner.id = :ownerId
                   and p.deletedAt is null
                """);

        if (search != null) {
            filters.append(" and (lower(p.name) like :search or lower(p.targetUrl) like :search)");
        }
        if (method != null) {
            filters.append(" and p.httpMethod = :method");
        }
        return filters.toString();
    }

    private void setTestPlanFilterParameters(
            jakarta.persistence.Query query,
            String search,
            HttpMethodType method
    ) {
        if (search != null) {
            query.setParameter("search", "%" + search.toLowerCase() + "%");
        }
        if (method != null) {
            query.setParameter("method", method);
        }
    }


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

    public TestPlan saveTestPlan(TestPlan testPlan) {
        em.persist(testPlan);
        em.flush();
        return testPlan;
    }

    public TestRun saveTestRun(TestRun run) {
        em.persist(run);
        em.flush();
        return run;
    }
}
