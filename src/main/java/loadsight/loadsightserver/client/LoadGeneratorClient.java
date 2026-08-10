package loadsight.loadsightserver.client;

import loadsight.loadsightserver.dto.GeneratorRunRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
public class LoadGeneratorClient {

    private final WebClient webClient;

    public LoadGeneratorClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public void start(GeneratorRunRequest request) {
        webClient.post()
                .uri("run")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block(Duration.ofSeconds(10));
    }
}
