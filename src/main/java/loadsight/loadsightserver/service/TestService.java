package loadsight.loadsightserver.service;

import loadsight.loadsightserver.config.WebClientConfig;
import loadsight.loadsightserver.domain.RunEntity;
import loadsight.loadsightserver.domain.RunStatus;
import loadsight.loadsightserver.domain.TestEntity;
import loadsight.loadsightserver.dto.TestRequest;
import loadsight.loadsightserver.dto.TestResponse;
import loadsight.loadsightserver.repository.RunRepository;
import loadsight.loadsightserver.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class TestService {

    @Autowired
    TestRepository testRepository;
    @Autowired
    RunRepository runRepository;
    @Autowired
    WebClient webClient;

    @Transactional
    public UUID save(TestRequest request){
        try {
            TestEntity test = new TestEntity();

            Map<String, Object> specMap = new HashMap<>();
            specMap.put("method", request.getMethod());
            specMap.put("threads", request.getThreads());
            specMap.put("totalRequest", request.getTotalRequest());
            specMap.put("requestInterval", request.getRequestInterval());

            test.setName(request.getName());
            test.setDescription(request.getDescriptions());
            test.setTargetUrl(request.getTargetUrl());
            test.setSpecJson(specMap);
            test.setDeleted(false);
            test.setBody(request.getData());
            test.setAuthType(request.getAuthType());
            test.setAuth(request.getAuth());

            testRepository.save(test);
            RunEntity run = runRepository.save(test);

            return run.getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void requestToGenerator(UUID runUuid, TestRequest request) {
        try {
            Map<String, Object> singleScenario = new HashMap<>();
            singleScenario.put("method", request.getMethod());
            singleScenario.put("path", request.getTargetUrl());
            if (request.getHeaders() != null) {
                singleScenario.put("headers", request.getHeaders());
            }
            if (request.getData() != null) {
                singleScenario.put("json", request.getData());
            }

            Map<String, Object> execCreateRequest = new HashMap<>();
            execCreateRequest.put("base_url", request.getTargetUrl());
            execCreateRequest.put(
                    "duration_s",
                    (double)request.getTotalRequest() * (double)request.getRequestInterval() / 1000
            ); // 총 요청 수 * (요청 간격 / 1000)
            execCreateRequest.put("rps", 1000.0 / (double)request.getRequestInterval()); // 1 / (요청간격 / 1000)
            execCreateRequest.put("concurrency",request.getThreads());
            execCreateRequest.put("run_id", runUuid.toString());
            execCreateRequest.put("scenario", singleScenario);

            String result = webClient.post()
                    .uri("run")
                    .bodyValue(execCreateRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public List<TestEntity> getAllTest() {
        try {
            List<TestEntity> allTests = testRepository.getAllTest();
            return allTests;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public TestEntity getTestById(Long id) {
        TestEntity test = testRepository.getTest(id);
        if (test != null) {
            return test;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 test id");
        }
    }

    @Transactional
    public void deleteTestById(Long id) {
        testRepository.deleteTestById(id);
    }
}
