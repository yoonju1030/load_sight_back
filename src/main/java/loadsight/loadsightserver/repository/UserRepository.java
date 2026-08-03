package loadsight.loadsightserver.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import loadsight.loadsightserver.domain.auth.entity.AppUser;
import loadsight.loadsightserver.domain.auth.entity.UserCredential;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager em;

    public boolean existsByEmail(String email) {
        Long count = em.createQuery(
                        "select count(u) from AppUser u where u.email = :email",
                        Long.class
                )
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    public void save(AppUser user, UserCredential credential) {
        em.persist(user);
        em.persist(credential);
        em.flush();
    }
}
