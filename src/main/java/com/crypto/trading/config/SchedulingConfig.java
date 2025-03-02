package com.crypto.trading.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    // The @EnableScheduling annotation tells Spring to look for @Scheduled methods
}