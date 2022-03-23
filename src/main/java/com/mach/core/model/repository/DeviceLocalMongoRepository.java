package com.mach.core.model.repository;

import com.mach.core.model.DeviceLocal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceLocalMongoRepository extends MongoRepository<DeviceLocal, String> {

    DeviceLocal findFirstByDeviceUdidLocal(String UDIDLocal);

}
