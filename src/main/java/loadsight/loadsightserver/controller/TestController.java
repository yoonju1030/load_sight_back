package loadsight.loadsightserver.controller;

import loadsight.loadsightserver.domain.TestEntity;
import loadsight.loadsightserver.dto.TestRequest;
import loadsight.loadsightserver.dto.TestResponse;
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

    @GetMapping()
    public ResponseEntity<List<TestEntity>> getAllTest() {
        try {
            List<TestEntity> result = testService.getAllTest();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{testId}")
    public ResponseEntity<TestEntity> getTest(@PathVariable("testId") Long id) {
        TestEntity test = testService.getTestById(id);
        return ResponseEntity.ok(test);
    }

    @GetMapping("/delete/{testId}")
    public ResponseEntity<Boolean> deleteTest(@PathVariable("testId") Long id) {
        testService.deleteTestById(id);
        return ResponseEntity.ok(true);
    }

}

