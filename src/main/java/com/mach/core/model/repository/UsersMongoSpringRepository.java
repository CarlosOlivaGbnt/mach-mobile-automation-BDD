package com.mach.core.model.repository;

import com.mach.core.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersMongoSpringRepository extends MongoRepository<User, String> {

    User findFirstByName(String name);

    User findFirstByAccountRUT(String rut);
}
