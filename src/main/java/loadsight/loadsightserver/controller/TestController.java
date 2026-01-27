package loadsight.loadsightserver.controller;

import loadsight.loadsightserver.dto.TestRequest;
import loadsight.loadsightserver.dto.TestResponse;
import loadsight.loadsightserver.service.LoadTestService;
import jakarta.validation.Valid;
import loadsight.loadsightserver.service.TestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
@CrossOrigin(origins = "*")
public class TestController {
    
    private final TestService testService;

    public TestController(
            TestService testService
    ) {
        this.testService = testService;
    }

    @PostMapping("")
    public ResponseEntity<TestResponse> create(@Valid @RequestBody TestRequest request) {
        testService.save(request);
        TestResponse resp = new TestResponse();
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}

