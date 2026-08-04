package loadsight.loadsightserver.dto;

import loadsight.loadsightserver.domain.auth.entity.AppUser;

import java.util.UUID;

public record LoginResponse(UUID id, String name, String email) {
    public static LoginResponse from(AppUser user) {
        return new LoginResponse(user.getId(), user.getDisplayName(), user.getEmail());
    }
}
