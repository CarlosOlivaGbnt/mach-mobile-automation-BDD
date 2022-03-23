package com.mach.core.model.repository;

import com.mach.core.model.SuiteResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SuiteResultRepository extends MongoRepository<SuiteResult, String> {

}
