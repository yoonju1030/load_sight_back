package loadsight.loadsightserver.domain.auth.entity;

import jakarta.persistence.*;
import loadsight.loadsightserver.domain.auth.enums.UserStatus;
import loadsight.loadsightserver.domain.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;

@Entity
@Getter
@Table(name = "app_user")
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class AppUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="email", nullable = false, unique = true, columnDefinition = "citext")
    private String email;

    @Column(name="display_name", nullable = false, length = 80)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false, length = 20)
    private UserStatus userStatus;

    @Column(name= "failed_login_count", nullable = false)
    private int failedLoginCount;

    @Column(name="locked_until")
    private OffsetDateTime lockedUntil;

    @Column(name="last_login_at")
    private OffsetDateTime lastLoginAt;

    @Builder
    private AppUser(String email, String displayName, UserStatus status) {
        this.email = normalizeEmail(email);
        this.displayName = displayName;
        this.userStatus = status;
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    public void activate() {
        this.userStatus = UserStatus.ACTIVE;
        this.lockedUntil = null;
        this.failedLoginCount = 0;
    }

    public void recordLoginSuccess(OffsetDateTime now) {
        this.failedLoginCount = 0;
        this.lockedUntil = null;
        this.lastLoginAt = now;
        if (this.userStatus == UserStatus.LOCKED) {
            this.userStatus = UserStatus.ACTIVE;
        }
    }

    public void recordLoginFailure() {
        this.failedLoginCount += 1;
    }

    public void lockUntil(OffsetDateTime until) {
        this.userStatus = UserStatus.LOCKED;
        this.lockedUntil = until;
    }

    public boolean isLoginAllowed(OffsetDateTime now) {
        if (this.userStatus == UserStatus.WITHDRAWN || this.userStatus == UserStatus.PENDING) return false;
        return this.userStatus != UserStatus.LOCKED || this.lockedUntil == null || !this.lockedUntil.isAfter(now);
    }
}
