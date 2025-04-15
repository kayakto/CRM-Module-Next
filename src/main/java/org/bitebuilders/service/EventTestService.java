package org.bitebuilders.service;

import org.bitebuilders.exception.EventTestNotFoundException;
import org.bitebuilders.model.EventTest;
import org.bitebuilders.repository.EventTestRepository;
import org.bitebuilders.repository.TestResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventTestService {

    private final EventTestRepository testRepository;

    @Autowired
    public EventTestService(EventTestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public EventTest getEventTest(Long eventId) {
        return testRepository.findByEventId(eventId)
                .orElseThrow(() -> new EventTestNotFoundException("There is not test for this event"));
    }

    public String getTestUrlByEventId(Long eventId) {
        return getEventTest(eventId).getTestUrl();
    }

    public EventTest addOrChangeTest(EventTest eventTest) {
        return testRepository.save(eventTest);
    }

    public EventTest createOrUpdateEventTest(Long eventId, String testUrl) {

        Optional<EventTest> testOptional = testRepository.findByEventId(eventId);

        if (testOptional.isPresent()) {
            EventTest existingTest = testOptional.get();
            existingTest.setTestUrl(testUrl);
            return testRepository.save(existingTest);
        }

        EventTest newTest = new EventTest(eventId, testUrl);
        return testRepository.save(newTest); // Сохраняем новый тест
    }
}
