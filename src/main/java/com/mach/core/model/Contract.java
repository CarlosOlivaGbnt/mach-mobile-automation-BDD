package com.mach.core.model;

import java.util.List;

public class Contract {
    private String type;
    private String platform;
    private List<String> contracts;
    private String desc;

    public Contract() {
        super();
    }

    public Contract(final String type, final String platform, final List<String> contracts, final String desc) {
        setType(type);
        setPlatform(platform);
        setContracts(contracts);
        setDesc(desc);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public List<String> getContracts() {
        return contracts;
    }

    public void setContracts(List<String> contracts) {
        this.contracts = contracts;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
