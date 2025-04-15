package org.bitebuilders.repository;

import org.bitebuilders.model.StudentTestResult;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestResultRepository extends CrudRepository<StudentTestResult, Long> {
}
