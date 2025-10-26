package com.spring.getready.config;

import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration to disable metrics in production to prevent container cgroup issues
 */
@Configuration
@Profile("prod")
@EnableAutoConfiguration(exclude = {
    MetricsAutoConfiguration.class,
    SystemMetricsAutoConfiguration.class,
    SimpleMetricsExportAutoConfiguration.class
})
public class MetricsConfig {
    // Metrics are completely disabled in production to prevent ProcessorMetrics
    // NullPointerException that occurs in containerized environments like Railway
    // where cgroup v2 information is not properly accessible
}