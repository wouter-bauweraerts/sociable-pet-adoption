package io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wouterbauweraerts.samples.sociablepetadoption.TestcontainersConfiguration;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.OwnerResponse;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api.request.AddOwnerRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api.request.UpdateOwnerRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.domain.Owner;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.domain.OwnerFixtures;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.repository.OwnerRepository;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class OwnerControllerTest {
    @Autowired
    MockMvcTester mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OwnerRepository ownerRepository;

    @BeforeEach
    void setUp() {
        ownerRepository.deleteAll();
    }

    @Test
    void listOwners() {
        List<Owner> owners = ownerRepository.saveAll(OwnerFixtures.getOwnersJpa(5));

        List<OwnerResponse> result = owners.stream()
                .map(o -> new OwnerResponse(o.getId(), o.getName(), Map.of()))
                .toList();

        assertThat(mockMvc.get().uri("/owners"))
                .hasStatus(OK)
                .bodyJson()
                .extractingPath("content")
                .convertTo(InstanceOfAssertFactories.list(OwnerResponse.class))
                .containsExactlyElementsOf(result);
    }

    @Test
    void getOwnerWithExistingOwner_returnsExpected() throws Exception {
        Owner owner = ownerRepository.save(OwnerFixtures.anOwnerJpa());
        OwnerResponse ownerResponse = new OwnerResponse(owner.getId(), owner.getName(), Map.of());

        assertThat(mockMvc.get().uri("/owners/%d".formatted(ownerResponse.getId())))
                .hasStatus(OK)
                .bodyJson()
                .isEqualTo(objectMapper.writeValueAsString(ownerResponse));
    }

    @Test
    void getOwnerWithNonExistingOwner_returns404() {
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

        assertThat(
                mockMvc.post().uri("/owners")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addOwner))
        ).hasStatus(CREATED)
                .bodyJson()
                .hasPathSatisfying("$.id", value -> value.assertThat().isNotNull())
                .hasPathSatisfying("$.name", value -> value.assertThat().isEqualTo(addOwner.name()))
                .extractingPath("id")
                .convertTo(Integer.class)
                .satisfies(id -> assertThat(ownerRepository.findById(id))
                        .hasValueSatisfying(o -> assertThat(o.getName()).isEqualTo(addOwner.name()))
                );
    }

    @Test
    void updateOwner_callsService() throws Exception {
        Owner owner = ownerRepository.save(OwnerFixtures.anOwnerJpa());
        UpdateOwnerRequest updateOwner = new UpdateOwnerRequest("Maria");
        assertThat(
                mockMvc.put().uri("/owners/%d".formatted(owner.getId()))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateOwner))
        ).hasStatus(NO_CONTENT);

        assertThat(ownerRepository.findById(owner.getId()))
                .hasValueSatisfying(o -> assertThat(o.getName()).isEqualTo("Maria"));
    }

    @Test
    void deleteOwner_callsService() {
        Owner owner = ownerRepository.save(OwnerFixtures.anOwnerJpa());
        assertThat(
                mockMvc.delete().uri("/owners/%d".formatted(owner.getId()))
        ).hasStatus(NO_CONTENT);
        assertThat(ownerRepository.findById(owner.getId())).isEmpty();
    }
}