package org.bitebuilders;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootTest(properties = "spring.task.scheduling.enabled=false")
public class UralinternCRMApiApplicationTest {
    @Test
    void contextLoads() {

    }
}
