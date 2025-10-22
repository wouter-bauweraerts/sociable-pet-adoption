package io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wouterbauweraerts.samples.sociablepetadoption.TestcontainersConfiguration;
import io.github.wouterbauweraerts.samples.sociablepetadoption.common.PetType;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetResponse;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api.request.AddPetRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api.request.AddPetRequestFixtures;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.Pet;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetFixtureBuilder;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetFixtures;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.repository.PetRepository;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PetControllerTest {
    @Autowired
    MockMvcTester mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    PetRepository petRepository;

    @Test
    void listPetsReturnsExpected() {
        List<Pet> pets = PetFixtures.getPets(3);

        when(petRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(pets));

        assertThat(mockMvc.get().uri("/pets"))
                .hasStatus(OK)
                .bodyJson()
                .hasPathSatisfying("$.content.[0].id", value -> value.assertThat().isEqualTo(pets.getFirst().getId()))
                .hasPathSatisfying("$.content.[0].name", value -> value.assertThat().isEqualTo(pets.getFirst().getName()))
                .hasPathSatisfying("$.content.[0].type", value -> value.assertThat().isEqualTo(pets.getFirst().getType().name()))
                .hasPathSatisfying("$.content.[1].id", value -> value.assertThat().isEqualTo(pets.get(1).getId()))
                .hasPathSatisfying("$.content.[1].name", value -> value.assertThat().isEqualTo(pets.get(1).getName()))
                .hasPathSatisfying("$.content.[1].type", value -> value.assertThat().isEqualTo(pets.get(1).getType().name()))
                .hasPathSatisfying("$.content.[2].id", value -> value.assertThat().isEqualTo(pets.getLast().getId()))
                .hasPathSatisfying("$.content.[2].name", value -> value.assertThat().isEqualTo(pets.getLast().getName()))
                .hasPathSatisfying("$.content.[2].type", value -> value.assertThat().isEqualTo(pets.getLast().getType().name()));
    }

    @Test
    void getPetWithNonExistingId_returns404() {
        when(petRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThat(mockMvc.get().uri("/pets/666"))
                .hasStatus(NOT_FOUND);
    }

    @Test
    void getPetWithExistingPet_returnsExpected() {
        Pet pet = PetFixtures.getPet();
        when(petRepository.findById(anyInt())).thenReturn(Optional.of(pet));

        assertThat(mockMvc.get().uri("/pets/%d".formatted(pet.getId())))
                .hasStatus(OK)
                .bodyJson()
                .hasPathSatisfying("$.id", value -> value.assertThat().isEqualTo(pet.getId()))
                .hasPathSatisfying("$.name", value -> value.assertThat().isEqualTo(pet.getName()))
                .hasPathSatisfying("$.type", value -> value.assertThat().isEqualTo(pet.getType().name()));
    }

    @Test
    void findAvailablePetsReturnsExpected_fromResource() {
        List<Pet> pets = PetFixtures.getAdoptablePets(3);

        when(petRepository.findPetsAvailableForAdoption(any(Pageable.class))).thenReturn(new PageImpl<>(pets));

        assertThat(mockMvc.get().uri("/pets/available-for-adoption"))
                .hasStatus(OK)
                .bodyJson()
                .hasPathSatisfying("$.content.[0].id", value -> value.assertThat().isEqualTo(pets.getFirst().getId()))
                .hasPathSatisfying("$.content.[0].name", value -> value.assertThat().isEqualTo(pets.getFirst().getName()))
                .hasPathSatisfying("$.content.[0].type", value -> value.assertThat().isEqualTo(pets.getFirst().getType().name()))
                .hasPathSatisfying("$.content.[1].id", value -> value.assertThat().isEqualTo(pets.get(1).getId()))
                .hasPathSatisfying("$.content.[1].name", value -> value.assertThat().isEqualTo(pets.get(1).getName()))
                .hasPathSatisfying("$.content.[1].type", value -> value.assertThat().isEqualTo(pets.get(1).getType().name()))
                .hasPathSatisfying("$.content.[2].id", value -> value.assertThat().isEqualTo(pets.getLast().getId()))
                .hasPathSatisfying("$.content.[2].name", value -> value.assertThat().isEqualTo(pets.getLast().getName()))
                .hasPathSatisfying("$.content.[2].type", value -> value.assertThat().isEqualTo(pets.getLast().getType().name()));
    }

    @TestFactory
    Stream<DynamicTest> addPetWithInvalidRequest_returnsBadRequestStatus() {
        return Stream.of(
                new AddPetRequest(null, null),
                new AddPetRequest("", null),
                new AddPetRequest(" ", null),
                new AddPetRequest("Goofy", null),
                new AddPetRequest(null, "CAT"),
                new AddPetRequest("Mickey", "MOUSE")
        ).map(req -> DynamicTest.dynamicTest(
                "addPet with invalid request %s returns BadRequest".formatted(req),
                () -> {
                    try {
                        assertThat(
                                mockMvc.post()
                                        .uri("/pets")
                                        .contentType(APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(req))
                        ).hasStatus(BAD_REQUEST);
                    } catch (JsonProcessingException e) {
                        fail("Unexpected JsonProcessingException", e);
                    }
                }
        ));
    }

    @Test
    void addPet_withValidRequest_callsServiceToCreateNewPet() throws Exception{
        AddPetRequest addPetRequest = AddPetRequestFixtures.anAddPetRequest();
        Pet entity = PetFixtureBuilder.fixtureBuilder()
                .withId(1)
                .withName(addPetRequest.name())
                .withType(PetType.valueOf(addPetRequest.type()))
                .build();

        PetResponse expectedResponse = new PetResponse(entity.getId(), addPetRequest.name(), addPetRequest.type());

        when(petRepository.save(any())).thenReturn(entity);

        assertThat(
                mockMvc.post().uri("/pets")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addPetRequest))
        ).hasStatus(CREATED)
                .bodyJson()
                .hasPathSatisfying("$.id", value -> value.assertThat().isEqualTo(expectedResponse.id()))
                .hasPathSatisfying("$.name", value -> value.assertThat().isEqualTo(expectedResponse.name()))
                .hasPathSatisfying("$.type", value -> value.assertThat().isEqualTo(expectedResponse.type()));

//        verify(petRepository).save(addPetRequest);
    }
}