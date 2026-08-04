package loadsight.loadsightserver.service;

import loadsight.loadsightserver.domain.auth.entity.AppUser;
import loadsight.loadsightserver.domain.auth.entity.UserCredential;
import loadsight.loadsightserver.domain.auth.enums.PasswordAlgorithm;
import loadsight.loadsightserver.domain.auth.enums.UserStatus;
import loadsight.loadsightserver.dto.SignupRequest;
import loadsight.loadsightserver.dto.SignupResponse;
import loadsight.loadsightserver.dto.LoginRequest;
import loadsight.loadsightserver.dto.LoginResponse;
import loadsight.loadsightserver.exception.EmailAlreadyExistsException;
import loadsight.loadsightserver.exception.InvalidCredentialsException;
import loadsight.loadsightserver.exception.LoginNotAllowedException;
import loadsight.loadsightserver.exception.PasswordTooLongException;
import loadsight.loadsightserver.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.time.OffsetDateTime;

@Service
public class UserService {

    private static final int BCRYPT_MAX_PASSWORD_BYTES = 72;
    private static final int MAX_LOGIN_FAILURES = 5;
    private static final int LOCK_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }

        validateBcryptPasswordLength(request.password());

        AppUser user = AppUser.builder()
                .email(email)
                .displayName(request.name().trim())
                .status(UserStatus.ACTIVE)
                .build();
        UserCredential credential = new UserCredential(
                user,
                passwordEncoder.encode(request.password()),
                PasswordAlgorithm.BCRYPT,
                false
        );

        try {
            userRepository.save(user, credential);
        } catch (DataIntegrityViolationException exception) {
            throw new EmailAlreadyExistsException();
        }

        return SignupResponse.from(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        UserCredential credential = userRepository.findCredentialByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        AppUser user = credential.getUser();
        OffsetDateTime now = OffsetDateTime.now();

        if (!user.isLoginAllowed(now)) {
            throw new LoginNotAllowedException(user.getLockedUntil());
        }

        if (!passwordEncoder.matches(request.password(), credential.getPasswordHash())) {
            user.recordLoginFailure();
            if (user.getFailedLoginCount() >= MAX_LOGIN_FAILURES) {
                user.lockUntil(now.plusMinutes(LOCK_MINUTES));
            }
            throw new InvalidCredentialsException();
        }

        user.recordLoginSuccess(now);
        return LoginResponse.from(user);
    }

    private void validateBcryptPasswordLength(String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > BCRYPT_MAX_PASSWORD_BYTES) {
            throw new PasswordTooLongException();
        }
    }
}
