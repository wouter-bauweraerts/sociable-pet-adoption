package io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import tools.jackson.databind.ObjectMapper;
import io.github.wouterbauweraerts.samples.sociablepetadoption.TestcontainersConfiguration;
import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.event.PetAdoptedEvent;
import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.request.AdoptPetCommand;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.domain.Owner;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.domain.OwnerFixtures;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.repository.OwnerRepository;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.Pet;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetFixtures;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@AutoConfigureMockMvc
@RecordApplicationEvents
@Import(TestcontainersConfiguration.class)
class AdoptionControllerTest {
    @Autowired
    MockMvcTester mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PetRepository petRepository;
    @Autowired
    OwnerRepository ownerRepository;

    @BeforeEach
    void setUp() {
        petRepository.deleteAll();
        ownerRepository.deleteAll();
    }

    @Test
    void searchAdoptablePetWithoutParametersReturnsOk() {
        assertThat(mockMvc.get().uri("/adoptions/search"))
                .hasStatus(OK);
    }

    @Test
    void searchAdoptablePetWithoutInvalidType_returnsBadRequest() {
        assertThat(mockMvc.get().uri("/adoptions/search").queryParam("types", "CAR"))
                .hasStatus(BAD_REQUEST);
    }

    @Test
    void searchAdoptablePetWithMultipleTypes_callsWithExpected() {
        assertThat(
                mockMvc.get()
                        .uri("/adoptions/search")
                        .queryParam("types", "DOG", "CAT")
        ).hasStatus(OK);
    }

    @Test
    void searchAdoptablePetWithMultipleSearchParams_callsWithExpected() {
        assertThat(
                mockMvc.get()
                        .uri("/adoptions/search")
                        .queryParam("types", "DOG", "CAT")
                        .queryParam("names", "Roxy", "Rex")
        ).hasStatus(OK);
    }

    @TestFactory
    Stream<DynamicTest> adoptWithInvalidCommand_badRequest() {
        return Stream.of(
                new AdoptPetCommand(null, null),
                new AdoptPetCommand(1, null),
                new AdoptPetCommand(null, 1),
                new AdoptPetCommand(1, -1),
                new AdoptPetCommand(-1, -1),
                new AdoptPetCommand(-1, 1)
        ).map(req -> dynamicTest(
                "POST to /adoptions with request %s returns HTTP400".formatted(req),
                () -> {
                    assertThat(
                            mockMvc.post().uri("/adoptions")
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(req))
                    ).hasStatus(BAD_REQUEST);
                }
        ));
    }

    @TestFactory
    Stream<DynamicTest> adoptWithValidRequest_callsService_whenServiceThrows_thenBadRequest() {
        Pet pet = petRepository.save(PetFixtures.getAdoptableJpaPet());
        Owner owner = ownerRepository.save(OwnerFixtures.anOwnerJpa());

        return Stream.of(
                new AdoptPetCommand(owner.getId() +1, pet.getId()),
                new AdoptPetCommand(owner.getId(), pet.getId() +1)
        ).map(cmd -> dynamicTest(
                "When pet or owner not found, controller returns HTTP400",
                () -> {
                    assertThat(
                            mockMvc.post().uri("/adoptions")
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(cmd))
                    ).hasStatus(BAD_REQUEST);
                }
        ));
    }

    @Test
    void adoptPet_noExceptionThrown_returnsNoContent(ApplicationEvents publishedEvents) throws Exception{
        Pet pet = petRepository.save(PetFixtures.getAdoptableJpaPet());
        Owner owner = ownerRepository.save(OwnerFixtures.anOwnerJpa());

        AdoptPetCommand adoptPetCommand = new AdoptPetCommand(owner.getId(), pet.getId());

        assertThat(
                mockMvc.post().uri("/adoptions")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoptPetCommand))
        ).hasStatus(NO_CONTENT);

        // Stream over all ApplicationEvents and filter by the packagename
        // I only want to know about events from my own code, not Spring or jUnit
        List<Object> applicationEvents = publishedEvents.stream(Object.class)
                .filter(e -> e.getClass().getPackageName().startsWith("io.github.wouterbauweraerts"))
                .toList();
        assertThat(applicationEvents)
                .containsExactly(
                new PetAdoptedEvent(owner.getId(), pet.getId())
        );
    }
}