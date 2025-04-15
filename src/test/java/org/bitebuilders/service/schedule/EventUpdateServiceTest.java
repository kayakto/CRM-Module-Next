package org.bitebuilders.service.schedule;

import org.bitebuilders.model.*;
import org.bitebuilders.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
@Transactional
class EventUpdateServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventUpdateService eventUpdateService;

    private final int expectedNumberOfGroups = 1;

    @BeforeEach
    void executeSqlFromFile() throws IOException {
        Resource resource = new ClassPathResource("test-data.sql");
        String sql = new String(Files.readAllBytes(resource.getFile().toPath()));
        jdbcTemplate.execute(sql);
    }

//    @Test
//    void testCheckAndCreateEventGroupsNoEvents() {
//        eventService.deleteAllEvents();
//
//        assertDoesNotThrow(() -> eventUpdateService.startEvents());
//
//        List<Event> events = eventService.getAllEvents();
//        assertTrue(events.isEmpty(), "No events should be present");
//    }
}


