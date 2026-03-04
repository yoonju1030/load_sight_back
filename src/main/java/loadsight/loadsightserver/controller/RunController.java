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

    @GetMapping("/getRunInfo/{runId}")
    public ResponseEntity<Boolean> getRunInfo(@PathVariable("runId") Long id) {
        /*
        * 필요한 run 정보
        * id: Date.now().toString(),
        timestamp: new Date().toISOString(),
        testName: config.value.testName || '',
        description: config.value.description || '',
        testConfig: {
          testName: config.value.testName || '',
          description: config.value.description || '',
          method: config.value.method,
          url: config.value.url,
          concurrentRequests: config.value.concurrentRequests,
          totalRequests: config.value.totalRequests,
          requestInterval: config.value.requestInterval,
          authType: config.value.authType
        },
        statistics: {
          totalRequests: stats.value.success + stats.value.failed,
          success: stats.value.success,
          failed: stats.value.failed,
          successRate: parseFloat(successRate.value),
          failureRate: parseFloat(failureRate.value),
          errorRate: parseFloat(errorRate.value),
          averageResponseTime: averageResponseTime.value,
          minResponseTime: stats.value.minTime === Infinity ? 0 : stats.value.minTime,
          maxResponseTime: stats.value.maxTime,
          p50Latency: p50Latency.value,
          p95Latency: p95Latency.value,
          duration: stats.value.endTime && stats.value.startTime
            ? (stats.value.endTime - stats.value.startTime) / 1000
            : 0,
          requestsPerSecond: parseFloat(requestsPerSecond.value)
        }
        * */
        return ResponseEntity.ok(true);
    }

    @GetMapping("/getStatistics/{runId}")
    public ResponseEntity<Boolean> getStatistics(@PathVariable("runId") String id) {
        runService.getStatistics(id);
        return ResponseEntity.ok(true);
    }
}
