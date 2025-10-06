package io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api.request;

import io.github.wouterbauweraerts.samples.sociablepetadoption.common.ValueOfEnum;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetType;
import jakarta.validation.constraints.NotBlank;

public record AddPetRequest(
        @NotBlank
        String name,
        @NotBlank
        @ValueOfEnum(enumClass = PetType.class)
        String type
) {
}
