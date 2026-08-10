package loadsight.loadsightserver.controller;

import jakarta.servlet.http.HttpSession;
import loadsight.loadsightserver.dto.PageResponse;
import loadsight.loadsightserver.dto.RecentRunResponse;
import loadsight.loadsightserver.service.TestRunService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/runs")
public class TestRunController {

    private static final int MAX_LIMIT = 100;

    private final TestRunService service;

    public TestRunController(TestRunService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PageResponse<RecentRunResponse>> getRuns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer days,
            HttpSession session
    ) {
        UUID userId = (UUID) session.getAttribute(UserController.AUTHENTICATED_USER_ID);
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 1), MAX_LIMIT);
        return ResponseEntity.ok(
                service.getRuns(
                        userId,
                        normalizedPage,
                        normalizedSize,
                        search,
                        status,
                        days
                )
        );
    }
}
