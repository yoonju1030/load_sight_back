package loadsight.loadsightserver.domain.auth.entity;

import jakarta.persistence.*;
import loadsight.loadsightserver.domain.auth.enums.PasswordAlgorithm;
import loadsight.loadsightserver.domain.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "user_credential")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCredential extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "password_algorithm", nullable = false, length = 20)
    private PasswordAlgorithm passwordAlgorithm = PasswordAlgorithm.ARGON2ID;

    @Column(name = "must_change_password", nullable = false)
    private boolean mustChangePassword;

    public UserCredential(AppUser user, String passwordHash,
                          PasswordAlgorithm passwordAlgorithm, boolean mustChangePassword) {
        this.user = user;
        this.passwordHash = passwordHash;
        this.passwordAlgorithm = passwordAlgorithm == null ? PasswordAlgorithm.ARGON2ID : passwordAlgorithm;
        this.mustChangePassword = mustChangePassword;
    }

    public void changePassword(String passwordHash, PasswordAlgorithm algorithm) {
        this.passwordHash = passwordHash;
        this.passwordAlgorithm = algorithm;
        this.mustChangePassword = false;
    }

    public void requirePasswordChange() {
        this.mustChangePassword = true;
    }

}
