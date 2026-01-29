package loadsight.loadsightserver.controller;

import jakarta.validation.Valid;
import loadsight.loadsightserver.domain.RunEntity;
import loadsight.loadsightserver.dto.ExistedRunRequest;
import loadsight.loadsightserver.dto.RunRequest;
import loadsight.loadsightserver.service.RunService;
import loadsight.loadsightserver.service.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/run")
@CrossOrigin(origins="*")
public class RunController {

    private final RunService runService;

    public RunController(RunService runService) {
        this.runService = runService;
    }

    @PostMapping("/start")
    public ResponseEntity<RunEntity> startRun(@Valid @RequestBody RunRequest request) {
        RunEntity createdRun = runService.startRun(request);
        return ResponseEntity.ok(createdRun);
    }

    @PostMapping("/stop")
    public ResponseEntity<Boolean> stopRun(@Valid @RequestBody ExistedRunRequest request) {
        runService.stopRun(request);
        return ResponseEntity.ok(true);
    }
}
