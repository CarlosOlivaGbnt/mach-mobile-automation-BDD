package com.mach.core.model.repository;

import com.mach.core.model.Contract;
import com.mach.core.util.EnumData;
import data.TestData;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ContractsRepository {

    private Map<String, Contract> map;

    public enum ContractsType {
        WITHOUT_MOVEMENTS("notFound"),
        VARIABLE_MONTHS("variableMovementsPerMonth"),
        NO_SERVICE("serverError");

        private String type;

        ContractsType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

    }

    public enum Platform {
        ANDROID("android"),
        IOS("ios");

        private String namePlatform;

        Platform(String namePlatform) {
            this.namePlatform = namePlatform;
        }

        public String getNamePlatform() {
            return namePlatform;
        }

    }

    public ContractsRepository() {
        map = new HashMap<>();
        List<Contract> contracts = TestData.getObjects(EnumData.CONTRACTS);
        contracts.forEach(a -> map.put(getKey(a.getType(),a.getPlatform()), a));
    }

    public String getContract(final ContractsType type, final Platform platform) {
        return getContracts(type,platform).get(0);
    }

    private List<String> getContracts(final ContractsType type, final Platform platform) {
        return map.get(getKey(type.getType(),platform.getNamePlatform())).getContracts();
    }

    private String getKey(final String type, final String platform){
        return type + "_" + platform;
    }

}
