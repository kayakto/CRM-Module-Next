package org.bitebuilders.repository;

import org.bitebuilders.model.EventTest;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventTestRepository extends CrudRepository<EventTest, Long> {
    @Query("SELECT * FROM events_tests et WHERE et.event_id = :eventId")
    Optional<EventTest> findByEventId(@Param("eventId") Long eventId);
}
