package loadsight.loadsightserver.controller;

import jakarta.servlet.http.HttpSession;
import loadsight.loadsightserver.dto.TestRunResponse;
import loadsight.loadsightserver.service.TestPlanRunService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/test-plans")
public class TestPlanRunController {

    private final TestPlanRunService service;

    public TestPlanRunController(TestPlanRunService service) {
        this.service = service;
    }

    @PostMapping("/{planId}/run")
    public ResponseEntity<TestRunResponse> createRun(@PathVariable UUID planId, HttpSession session) {
        UUID userId = (UUID) session.getAttribute(UserController.AUTHENTICATED_USER_ID);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createRun(planId, userId));
    }
}
