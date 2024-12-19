package com.web.student_register.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@ConfigurationProperties(prefix = "limiter")
public class RateLimiterConfig {
    private String domain;
    private List<Discriptors> discriptors;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<Discriptors> getDiscriptors() {
        return discriptors;
    }

    public void setDiscriptors(List<Discriptors> discriptors) {
        this.discriptors = discriptors;
    }
}
