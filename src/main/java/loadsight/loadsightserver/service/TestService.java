package loadsight.loadsightserver.service;

import loadsight.loadsightserver.domain.TestEntity;
import loadsight.loadsightserver.dto.TestRequest;
import loadsight.loadsightserver.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestService {

    @Autowired
    TestRepository testRepository;

    @Transactional
    public Long save(TestRequest request){
        TestEntity test = new TestEntity();

        Map<String, Object> testMap = new HashMap<>();
        testMap.put("method", request.getMethod());
        List<String> testList = new ArrayList<>();

        test.setName(request.getName());
        test.setDescription(request.getDescriptions());
        test.setSpecJson(testMap);
        test.setTags(testList);
        test.setDeleted(false);

        testRepository.save(test);
        return test.getId();
    }
}
