package org.bitebuilders.repository;

import org.bitebuilders.model.EventForm;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventFormRepository extends CrudRepository<EventForm, Long> {
    Optional<EventForm> findByEventId(Long eventId);
    void deleteByEventId(Long eventId);
}
