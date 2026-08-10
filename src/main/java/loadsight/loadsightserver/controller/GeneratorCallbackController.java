package loadsight.loadsightserver.controller;

import jakarta.validation.Valid;
import loadsight.loadsightserver.dto.GeneratorRunResultRequest;
import loadsight.loadsightserver.service.RunResultService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@RestController
@RequestMapping("/api/internal/v1/runs")
public class GeneratorCallbackController {

    private final RunResultService resultService;
    private final byte[] callbackToken;

    public GeneratorCallbackController(
            RunResultService resultService,
            @Value("${loadsight.generator.callback-token:change-me}") String callbackToken
    ) {
        this.resultService = resultService;
        this.callbackToken = callbackToken.getBytes(StandardCharsets.UTF_8);
    }

    @PostMapping("/{runId}/result")
    public ResponseEntity<Void> receiveResult(
            @PathVariable UUID runId,
            @RequestHeader(name = "X-LoadSight-Generator-Token", required = false) String token,
            @Valid @RequestBody GeneratorRunResultRequest request
    ) {
        byte[] provided = token == null ? new byte[0] : token.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(callbackToken, provided)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        resultService.store(runId, request);
        return ResponseEntity.noContent().build();
    }
}
