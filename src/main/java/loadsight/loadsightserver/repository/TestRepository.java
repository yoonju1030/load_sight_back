package loadsight.loadsightserver.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import loadsight.loadsightserver.domain.TestEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Repository
public class TestRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(TestEntity test) {
        em.persist(test);
    }

    public List<TestEntity> getAllTest() {
        try {
            return em.createQuery(
                    "select t from TestEntity t where t.deleted = :deleted", TestEntity.class
            ).setParameter("deleted", false).getResultList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public TestEntity getTest(Long testId) {
        try {
            return em.createQuery(
                    "select t from TestEntity t where t.id = :id", TestEntity.class
            ).setParameter("id", testId).getSingleResultOrNull();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteTestById(Long testId) {
        try {
           TestEntity test = em.find(TestEntity.class, testId);
            if (test == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "test not found: " + testId);
            }
            test.setDeleted(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
