package org.bitebuilders.service;

import org.bitebuilders.model.StudentTestResult;
import org.bitebuilders.repository.TestResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestResultService {
    private final TestResultRepository testResultRepository;


    @Autowired
    public TestResultService(TestResultRepository testResultRepository) {
        this.testResultRepository = testResultRepository;
    }

    public StudentTestResult addOrUpdateResult(StudentTestResult testResult) {
        return testResultRepository.save(testResult);
    }
}
