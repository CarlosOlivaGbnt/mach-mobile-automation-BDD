package com.mach.core.config;

import com.mach.core.exception.MachException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

public class MachProperties {

    private ResourcePropertySource properties;

    private static MachProperties instance;

    private MachProperties() {
        try {
            Resource propertiesResource = new ClassPathResource("appium.properties");
            properties = new ResourcePropertySource(propertiesResource);
        } catch (IOException e) {
            throw new MachException("Unable to read pom file for properties", e);
        }
    }

    public static synchronized MachProperties getInstance() {
        if(instance == null) {
            instance = new MachProperties();
        }
        return instance;
    }

    public String getString(String propertyName) {
        if (null != System.getenv(propertyName)) {
            return System.getenv(propertyName);
        } else if (null != System.getProperty(propertyName)) {
            return System.getProperty(propertyName);
        } else {
            return String.valueOf(properties.getProperty(propertyName));
        }
    }

    public String getString(String propertyName, String defaultValue) {
        String value = getString(propertyName);
        return value == null ? defaultValue : value;
    }

    public int getInteger(String propertyName) {
        return Integer.valueOf(getString(propertyName));
    }

    public double getDouble(String propertyName) {
        return Double.valueOf(getString(propertyName));
    }

    public boolean getBoolean(String propertyName) {
        return Boolean.valueOf(getString(propertyName));
    }

}
