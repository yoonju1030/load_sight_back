package loadsight.loadsightserver.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import loadsight.loadsightserver.domain.loadtest.entity.TestRun;
import loadsight.loadsightserver.domain.loadtest.enums.RunStatus;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public class TestRunRepository {

    @PersistenceContext
    private EntityManager em;

    public List<TestRun> findPageByOwner(
            UUID ownerId,
            String search,
            UUID runId,
            Set<RunStatus> statuses,
            OffsetDateTime createdAfter,
            int page,
            int size
    ) {
        String filters = runFilters(search, runId, statuses, createdAfter);
        var query = em.createQuery(
                        "select r " + filters + " order by r.createdAt desc",
                        TestRun.class
                )
                .setParameter("ownerId", ownerId)
                .setFirstResult(page * size)
                .setMaxResults(size);

        setRunFilterParameters(query, search, runId, statuses, createdAfter);
        return query.getResultList();
    }

    public long countByOwner(
            UUID ownerId,
            String search,
            UUID runId,
            Set<RunStatus> statuses,
            OffsetDateTime createdAfter
    ) {
        String filters = runFilters(search, runId, statuses, createdAfter);
        var query = em.createQuery("select count(r) " + filters, Long.class)
                .setParameter("ownerId", ownerId);

        setRunFilterParameters(query, search, runId, statuses, createdAfter);
        return query.getSingleResult();
    }

    private String runFilters(
            String search,
            UUID runId,
            Set<RunStatus> statuses,
            OffsetDateTime createdAfter
    ) {
        StringBuilder filters = new StringBuilder("""
                 from TestRun r
                 join r.testPlan p
                 where r.owner.id = :ownerId
                """);

        if (search != null) {
            filters.append(" and (lower(p.name) like :search");
            if (runId != null) {
                filters.append(" or r.id = :runId");
            }
            filters.append(")");
        }
        if (statuses != null) {
            filters.append(" and r.status in :statuses");
        }
        if (createdAfter != null) {
            filters.append(" and r.createdAt >= :createdAfter");
        }
        return filters.toString();
    }

    private void setRunFilterParameters(
            jakarta.persistence.Query query,
            String search,
            UUID runId,
            Set<RunStatus> statuses,
            OffsetDateTime createdAfter
    ) {
        if (search != null) {
            query.setParameter("search", "%" + search.toLowerCase() + "%");
            if (runId != null) {
                query.setParameter("runId", runId);
            }
        }
        if (statuses != null) {
            query.setParameter("statuses", statuses);
        }
        if (createdAfter != null) {
            query.setParameter("createdAfter", createdAfter);
        }
    }
}
