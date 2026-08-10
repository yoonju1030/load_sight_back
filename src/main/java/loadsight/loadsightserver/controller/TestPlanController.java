package loadsight.loadsightserver.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import loadsight.loadsightserver.dto.PageResponse;
import loadsight.loadsightserver.dto.TestPlanCreateRequest;
import loadsight.loadsightserver.dto.TestPlanCreateResponse;
import loadsight.loadsightserver.dto.TestPlanListResponse;
import loadsight.loadsightserver.dto.TestRunResponse;
import loadsight.loadsightserver.service.TestPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/test-plans")
public class TestPlanController {

    private final TestPlanService service;

    public TestPlanController(TestPlanService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PageResponse<TestPlanListResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String method,
            HttpSession session
    ) {
        UUID userId = (UUID) session.getAttribute(UserController.AUTHENTICATED_USER_ID);
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        return ResponseEntity.ok(
                service.getAll(userId, normalizedPage, normalizedSize, search, method)
        );
    }

    @PostMapping
    public ResponseEntity<TestPlanCreateResponse> create(
            @Valid @RequestBody TestPlanCreateRequest request,
            HttpSession session
    ) {
        UUID userId = (UUID) session.getAttribute(UserController.AUTHENTICATED_USER_ID);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, userId));
    }

    @PostMapping("/{planId}/run")
    public ResponseEntity<TestRunResponse> createRun(@PathVariable UUID planId, HttpSession session) {
        UUID userId = (UUID) session.getAttribute(UserController.AUTHENTICATED_USER_ID);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createRun(planId, userId));
    }
}
