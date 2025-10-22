package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wouterbauweraerts.samples.sociablepetadoption.TestClockConfig;
import io.github.wouterbauweraerts.samples.sociablepetadoption.common.PetType;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.api.request.RegisterVetCheckUpRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.Owner;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.OwnerRepository;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.Pet;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.PetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestClockConfig.class)
class VeterinaryControllerTest {
    @Autowired
    MockMvcTester mockMvc;

    @Autowired
    PetRepository petRepository;
    @Autowired
    OwnerRepository ownerRepository;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    Clock clock;

    @Test
    void registerCheckup_petNotFound() throws Exception {
        assertThat(mockMvc.patch().uri("/veterinary/checkups/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterVetCheckUpRequest(LocalDate.now(clock))))
                .exchange()
        ).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void registerCheckup_petFound() throws Exception {
        Owner john = ownerRepository.save(new Owner(null, 22, "John", new ArrayList<>()));
        Pet boomer = petRepository.save(new Pet(null, 123, "Boomer", PetType.DOG, john, LocalDate.of(2023, 1, 12)));

        john.adoptPet(boomer);

        assertThat(
        mockMvc.patch().uri("/veterinary/checkup/%d".formatted(boomer.getPetId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterVetCheckUpRequest(LocalDate.now(clock))))
        ).hasStatus(HttpStatus.OK);

        assertThat(petRepository.findById(boomer.getInternalId()))
                .hasValueSatisfying(pet -> assertThat(pet.getLastVetCheck()).isEqualTo(LocalDate.now(clock)));
    }
}