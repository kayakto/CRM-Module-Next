package org.bitebuilders.repository;

import org.bitebuilders.model.EventForm;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventFormRepository extends CrudRepository<EventForm, Long> {
    List<EventForm> findByEventId(Long eventId);
}
