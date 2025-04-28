package org.bitebuilders.repository;

import org.bitebuilders.model.Application;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends CrudRepository<Application, Long> {
    boolean existsByStatusId(Long statusId);
}
