package com.mach.core.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "device")
public class DeviceLocal {

    @Id
    private String id;

    private String deviceIdMACH;
    private String deviceUdidLocal;
    private String platform;
    private String model;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceIdMACH() {
        return deviceIdMACH;
    }

    public void setDeviceIdMACH(String deviceIdMACH) {
        this.deviceIdMACH = deviceIdMACH;
    }

    public String getDeviceUdidLocal() {
        return deviceUdidLocal;
    }

    public void setDeviceUdidLocal(String deviceUdidLocal) {
        this.deviceUdidLocal = deviceUdidLocal;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
