package io.github.wouterbauweraerts.samples.sociablepetadoption;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.Clock;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties
public class SpringPetAdoptionApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringPetAdoptionApplication.class, args);
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
