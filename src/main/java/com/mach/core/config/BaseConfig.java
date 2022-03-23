package com.mach.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("com.mach.*")
@EnableAspectJAutoProxy
public class BaseConfig {

    @Bean(name = "activeProfile")
    public String activeProfile() {
        return MachProfileResolver.getActiveProfile();
    }

}
