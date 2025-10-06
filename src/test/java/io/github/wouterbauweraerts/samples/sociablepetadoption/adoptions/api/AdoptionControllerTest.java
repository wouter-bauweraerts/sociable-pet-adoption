package io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.AdoptionService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.exceptions.OwnerNotFoundException;
import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.exceptions.PetNotFoundException;
import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.request.AdoptPetCommand;
import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.request.AdoptablePetSearch;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetService;

@WebMvcTest(AdoptionController.class)
class AdoptionControllerTest {
    @Autowired
    MockMvcTester mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    PetService petService;
    @MockitoBean
    AdoptionService adoptionService;

    @Captor
    ArgumentCaptor<AdoptablePetSearch> searchCaptor;

    @Test
    void searchAdoptablePetWithoutParametersReturnsOk() {
        assertThat(mockMvc.get().uri("/adoptions/search"))
                .hasStatus(OK);

        verify(petService).searchAdoptablePets(searchCaptor.capture(), any(Pageable.class));

        assertThat(searchCaptor.getValue())
                .returns(List.of(), AdoptablePetSearch::getTypes)
                .returns(List.of(), AdoptablePetSearch::getNames);
    }

    @Test
    void searchAdoptablePetWithoutInvalidType_returnsBadRequest() {
        assertThat(mockMvc.get().uri("/adoptions/search").queryParam("types", "CAR"))
                .hasStatus(BAD_REQUEST);

        verifyNoInteractions(petService);
    }

    @Test
    void searchAdoptablePetWithMultipleTypes_callsWithExpected() {
        assertThat(
                mockMvc.get()
                        .uri("/adoptions/search")
                        .queryParam("types", "DOG", "CAT")
        ).hasStatus(OK);

        verify(petService).searchAdoptablePets(searchCaptor.capture(), any(Pageable.class));

        assertThat(searchCaptor.getValue())
                .returns(List.of("DOG", "CAT"), AdoptablePetSearch::getTypes)
                .returns(List.of(), AdoptablePetSearch::getNames);
    }

    @Test
    void searchAdoptablePetWithMultipleSearchParams_callsWithExpected() {
        assertThat(
                mockMvc.get()
                        .uri("/adoptions/search")
                        .queryParam("types", "DOG", "CAT")
                        .queryParam("names", "Roxy", "Rex")
        ).hasStatus(OK);

        verify(petService).searchAdoptablePets(searchCaptor.capture(), any(Pageable.class));

        assertThat(searchCaptor.getValue())
                .returns(List.of("DOG", "CAT"), AdoptablePetSearch::getTypes)
                .returns(List.of("Roxy", "Rex"), AdoptablePetSearch::getNames);
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

                    verifyNoInteractions(adoptionService);
                }
        ));
    }

    @TestFactory
    Stream<DynamicTest> adoptWithValidRequest_callsService_whenServiceThrows_thenBadRequest() {
        AdoptPetCommand adoptPetCommand = new AdoptPetCommand(1, 1);
        return Stream.of(
                new PetNotFoundException("Pet %d is not available for adoption.".formatted(adoptPetCommand.petId())),
                new OwnerNotFoundException("Owner %d is not available for adoption.".formatted(adoptPetCommand.ownerId()))
        ).map(ex -> dynamicTest(
                "When service throws %s, controller returns HTTP400".formatted(ex.getClass().getSimpleName()),
                () -> {

                    doThrow(ex).when(adoptionService).adopt(any(AdoptPetCommand.class));

                    assertThat(
                            mockMvc.post().uri("/adoptions")
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(adoptPetCommand))
                    ).hasStatus(BAD_REQUEST);
                }
        ));
    }

    @Test
    void adoptPet_noExceptionThrown_returnsNoContent() throws Exception{
        AdoptPetCommand adoptPetCommand = new AdoptPetCommand(13, 666);

        doNothing().when(adoptionService).adopt(any(AdoptPetCommand.class));

        assertThat(
                mockMvc.post().uri("/adoptions")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoptPetCommand))
        ).hasStatus(NO_CONTENT);
    }
}