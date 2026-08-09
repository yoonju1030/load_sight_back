package loadsight.loadsightserver.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import loadsight.loadsightserver.dto.TestPlanCreateRequest;
import loadsight.loadsightserver.dto.TestPlanCreateResponse;
import loadsight.loadsightserver.service.TestPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/test-plans")
public class TestPlanController {

    private final TestPlanService service;

    public TestPlanController(TestPlanService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TestPlanCreateResponse> create(
            @Valid @RequestBody TestPlanCreateRequest request,
            HttpSession session
    ) {
        UUID userId = (UUID) session.getAttribute(UserController.AUTHENTICATED_USER_ID);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, userId));
    }
}
