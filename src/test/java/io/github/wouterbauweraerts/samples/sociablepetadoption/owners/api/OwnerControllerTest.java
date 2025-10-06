package io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.json.JsonCompareMode.LENIENT;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.OwnerService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api.request.AddOwnerRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api.request.UpdateOwnerRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api.response.OwnerResponse;

@WebMvcTest(OwnerController.class)
class OwnerControllerTest {
    @Autowired
    MockMvcTester mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    OwnerService ownerService;

    @Test
    void listOwners() {
        List<OwnerResponse> owners = List.of(
                new OwnerResponse(1, "Wouter", Map.of("DOG", List.of("Roxy"))),
                new OwnerResponse(2, "Josh", Map.of()),
                new OwnerResponse(3, "Alina", Map.of()),
                new OwnerResponse(4, "Venkat", Map.of()),
                new OwnerResponse(5, "Patrick", Map.of())
        );

        when(ownerService.getOwners(any(Pageable.class))).thenReturn(
                new PageImpl<>(owners)
        );

        assertThat(mockMvc.get().uri("/owners"))
                .hasStatus(OK)
                .bodyJson()
                .withResourceLoadClass(null)
                .isEqualTo("all-owners-paged.json", LENIENT);
    }

    @Test
    void getOwnerWithExistingOwner_returnsExpected() throws Exception {
        OwnerResponse ownerResponse = new OwnerResponse(1, "Wouter", Map.of("DOG", List.of("Roxy")));
        when(ownerService.getOwnerById(anyInt())).thenReturn(Optional.of(ownerResponse));

        assertThat(mockMvc.get().uri("/owners/%d".formatted(ownerResponse.getId())))
                .hasStatus(OK)
                .bodyJson()
                .isEqualTo(objectMapper.writeValueAsString(ownerResponse));
    }

    @Test
    void getOwnerWithNonExistingOwner_returns404() {
        when(ownerService.getOwnerById(anyInt())).thenReturn(Optional.empty());

        assertThat(mockMvc.get().uri("/owners/666"))
                .hasStatus(404);
    }

    @TestFactory
    Stream<DynamicTest> addOwner_invalidRequest_returnsBadRequest() {
        return Stream.of(
                        null,
                        "",
                        " ",
                        "\t",
                        "\n"
                ).map(AddOwnerRequest::new)
                .map(request -> dynamicTest(
                        "Add owner with name %s should return HTTP400".formatted(request.name()),
                        () -> assertThat(
                                mockMvc.post().uri("/owners")
                                        .contentType(APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                        ).hasStatus(BAD_REQUEST)
                ));
    }

    @Test
    void addOwner_returnsExpected() throws Exception {
        AddOwnerRequest addOwner = new AddOwnerRequest("Mario");
        OwnerResponse createdOwner = new OwnerResponse(13, "Mario", Map.of());

        when(ownerService.addOwner(any(AddOwnerRequest.class))).thenReturn(createdOwner);

        assertThat(
                mockMvc.post().uri("/owners")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addOwner))
        ).hasStatus(CREATED)
                .bodyJson()
                .isEqualTo(objectMapper.writeValueAsString(createdOwner));
    }

    @Test
    void updateOwner_callsService() throws Exception{
        UpdateOwnerRequest updateOwner = new UpdateOwnerRequest("Maria");
        assertThat(
                mockMvc.put().uri("/owners/13")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateOwner))
        ).hasStatus(NO_CONTENT);

        verify(ownerService).updateOwner(13, updateOwner);
    }

    @Test
    void deleteOwner_callsService() {
        assertThat(
                mockMvc.delete().uri("/owners/13")
        ).hasStatus(NO_CONTENT);

        verify(ownerService).deleteOwner(13);
    }
}