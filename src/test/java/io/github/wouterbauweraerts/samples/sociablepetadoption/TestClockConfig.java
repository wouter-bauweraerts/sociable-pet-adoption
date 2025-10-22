package io.github.wouterbauweraerts.samples.sociablepetadoption;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.*;

@TestConfiguration
public class TestClockConfig {
    @Bean
    @Primary
    public Clock testClock() {
        Instant instant = LocalDateTime.of(2025, 10, 12, 11, 12, 13, 456).toInstant(ZoneOffset.UTC);
        return Clock.fixed(instant, ZoneId.systemDefault());
    }
}
