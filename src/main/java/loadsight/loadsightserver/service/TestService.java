package loadsight.loadsightserver.service;

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
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class TestService {

    @Autowired
    TestRepository testRepository;
    @Autowired
    RunRepository runRepository;

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
            test.setSpecJson(specMap);
            test.setDeleted(false);
            test.setBody(request.getData());
            test.setAuthType(request.getAuthType());
            test.setAuth(request.getAuth());

            testRepository.save(test);
            runRepository.save(test);

            return test.getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public List<TestEntity> getAllTest() {
        List<TestEntity> allTests =  testRepository.getAllTest();
        return allTests;
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
