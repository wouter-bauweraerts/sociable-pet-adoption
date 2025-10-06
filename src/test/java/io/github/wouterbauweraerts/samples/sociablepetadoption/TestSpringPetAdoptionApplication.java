package io.github.wouterbauweraerts.samples.sociablepetadoption;

import org.springframework.boot.SpringApplication;

public class TestSpringPetAdoptionApplication {

    public static void main(String[] args) {
        SpringApplication.from(SpringPetAdoptionApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
