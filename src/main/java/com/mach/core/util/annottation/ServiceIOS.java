package com.mach.core.util.annottation;

import io.appium.java_client.remote.MobilePlatform;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Lazy
@Profile(MobilePlatform.IOS)
public @interface ServiceIOS {
}
