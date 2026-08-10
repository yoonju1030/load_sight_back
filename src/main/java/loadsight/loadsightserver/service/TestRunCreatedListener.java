package loadsight.loadsightserver.service;

import loadsight.loadsightserver.client.LoadGeneratorClient;
import loadsight.loadsightserver.dto.GeneratorRunRequest;
import loadsight.loadsightserver.event.TestRunCreatedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TestRunCreatedListener {

    private final TestRunExecutionService executionService;
    private final LoadGeneratorClient generatorClient;

    public TestRunCreatedListener(
            TestRunExecutionService executionService,
            LoadGeneratorClient generatorClient
    ) {
        this.executionService = executionService;
        this.generatorClient = generatorClient;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRunCreated(TestRunCreatedEvent event) {
        try {
            GeneratorRunRequest request = executionService.prepare(event.runId());
            generatorClient.start(request);
            executionService.markRunning(event.runId());
        } catch (Exception exception) {
            executionService.markFailed(event.runId(), exception.getMessage());
        }
    }
}
