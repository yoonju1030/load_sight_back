package loadsight.loadsightserver.exception;

import java.time.OffsetDateTime;

public class LoginNotAllowedException extends RuntimeException {
    public LoginNotAllowedException(OffsetDateTime lockedUntil) {
        super(lockedUntil == null
                ? "로그인할 수 없는 계정입니다."
                : "로그인 시도가 잠시 제한되었습니다. " + lockedUntil + " 이후 다시 시도해 주세요.");
    }
}
