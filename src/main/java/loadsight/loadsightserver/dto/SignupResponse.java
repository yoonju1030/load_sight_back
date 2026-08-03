package loadsight.loadsightserver.dto;

import loadsight.loadsightserver.domain.auth.entity.AppUser;
import loadsight.loadsightserver.domain.auth.enums.UserStatus;

import java.util.UUID;

public record SignupResponse(
        UUID id,
        String name,
        String email,
        UserStatus status
) {
    public static SignupResponse from(AppUser user) {
        return new SignupResponse(
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                user.getUserStatus()
        );
    }
}
