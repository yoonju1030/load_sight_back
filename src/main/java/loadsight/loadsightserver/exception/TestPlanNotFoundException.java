package loadsight.loadsightserver.exception;

public class TestPlanNotFoundException extends RuntimeException {
    public TestPlanNotFoundException() {
        super("테스트 플랜을 찾을 수 없습니다.");
    }
}
