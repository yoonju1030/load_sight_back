package loadsight.loadsightserver.exception;

public class PasswordTooLongException extends RuntimeException {
    public PasswordTooLongException() {
        super("비밀번호가 너무 깁니다.");
    }
}
