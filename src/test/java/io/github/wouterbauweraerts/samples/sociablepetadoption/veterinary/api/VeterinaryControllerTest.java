package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.api;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import tools.jackson.databind.ObjectMapper;
import io.github.wouterbauweraerts.samples.sociablepetadoption.TestClockConfig;
import io.github.wouterbauweraerts.samples.sociablepetadoption.common.NotFoundException;
import io.github.wouterbauweraerts.samples.sociablepetadoption.common.PetType;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.api.request.RegisterVetCheckUpRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.Owner;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.OwnerRepository;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.Pet;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.PetRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.context.WebApplicationContext;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestClockConfig.class)
class VeterinaryControllerTest {
    //    @Autowired
    MockMvcTester mockMvc;

    @Autowired
    WebApplicationContext ctx;

    @Autowired
    PetRepository petRepository;
    @Autowired
    OwnerRepository ownerRepository;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    Clock clock;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcTester.from(ctx, builder -> builder.addDispatcherServletCustomizer(servlet -> servlet.setEnableLoggingRequestDetails(true)).alwaysDo(print()).build());
    }

    @AfterEach
    void tearDown() {
        petRepository.deleteAll();
        ownerRepository.deleteAll();
    }

    @Test
    void registerCheckup_petNotFound() throws Exception {
        assertThat(mockMvc.patch().uri("/veterinary/checkups/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new RegisterVetCheckUpRequest(LocalDate.now(clock)))).exchange()).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void registerCheckup_petFound() throws Exception {
        Owner john = ownerRepository.save(new Owner(null, 22, "John", new ArrayList<>()));
        Pet boomer = petRepository.save(new Pet(null, 123, "Boomer", PetType.DOG, john, LocalDate.of(2023, 1, 12)));

        john.adoptPet(boomer);

        mockMvc.patch().uri("/veterinary/checkup/%d".formatted(boomer.getPetId())).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new RegisterVetCheckUpRequest(LocalDate.now(clock)))).assertThat().hasStatus(HttpStatus.OK);


        assertThat(petRepository.findById(boomer.getInternalId())).hasValueSatisfying(pet -> assertThat(pet.getLastVetCheck()).isEqualTo(LocalDate.now(clock)));
    }

    @Test
    void getCheckupPrice_ownerNotFound_returnsNotFound() {
        mockMvc.get().uri("/veterinary/123/checkup/234").assertThat().hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void getCheckupPrice_petNotFoundWithinOwner_returnsNotFound() {
        Owner john = ownerRepository.save(new Owner(null, 22, "John", new ArrayList<>()));

        mockMvc.get().uri("/veterinary/%d/checkup/234".formatted(john.getOwnerId())).assertThat().hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void getCheckupPrice_happyFlow() {
        Owner john = ownerRepository.save(new Owner(null, 22, "John", new ArrayList<>()));
        Pet boomer = petRepository.save(new Pet(null, 123, "Boomer", PetType.DOG, john, LocalDate.now(clock).minusMonths(1)));

        john.adoptPet(boomer);
        ownerRepository.save(john);

        String expectedResponse = """
                {
                    "ownerId": %d,
                    "petId": %d,
                    "price": %s
                }""".formatted(john.getOwnerId(), boomer.getPetId(), "50.00");

        mockMvc.get().uri("/veterinary/%d/checkup/%d".formatted(john.getOwnerId(), boomer.getPetId()))
                .assertThat()
                .hasStatusOk()
                .bodyJson()
                .isLenientlyEqualTo(expectedResponse);

    }
}