package io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal;

import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api.request.AddPetRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api.response.PetResponse;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.Pet;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetType;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mapstruct.factory.Mappers;
import org.springframework.data.util.Pair;

import java.util.Objects;
import java.util.stream.Stream;

import static io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetType.DOG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class PetMapperTest {
    PetMapper mapper = Mappers.getMapper(PetMapper.class);

    @Test
    void map_canHandleNull() {
        assertThat(mapper.map(null)).isNull();
    }

    @TestFactory
    Stream<DynamicTest> mapFromPetToPetResponse_returnsExpected() {
        return Stream.of(
                Pair.of(
                        new Pet(1, "Roxy", DOG, 1),
                        new PetResponse(1, "Roxy", DOG.name())
                ),
                Pair.of(
                        new Pet(1, null, DOG, null),
                        new PetResponse(1, null, DOG.name())
                ),
                Pair.of(
                        new Pet(1, "Roxy", null, 1),
                        new PetResponse(1, "Roxy", null)
                )
        ).map(pair -> dynamicTest(
                "%s maps to %s".formatted(pair.getFirst(), pair.getSecond()),
                () -> assertThat(mapper.map(pair.getFirst())).isEqualTo(pair.getSecond())
        ));
    }

    @TestFactory
    Stream<DynamicTest> toEntity_mapsToExpected() {
        return Stream.of(
                null,
                new AddPetRequest(null, "CAT"),
                new AddPetRequest("Michelangelo", "TURTLE")
        ).map(req -> dynamicTest(
                "%s is mapped to expected entity",
                () -> {
                    Pet pet = Objects.isNull(req) ? null : new Pet(null, req.name(), PetType.valueOf(req.type()), null);
                    assertThat(mapper.toEnity(req)).isEqualTo(pet);
                }
        ));
    }
}