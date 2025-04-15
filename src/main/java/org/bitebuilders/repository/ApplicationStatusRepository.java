package org.bitebuilders.repository;

import org.bitebuilders.model.ApplicationStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationStatusRepository extends CrudRepository<ApplicationStatus, Long> {
    
}
