package io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api.request;

import io.github.wouterbauweraerts.samples.sociablepetadoption.common.PetType;
import io.github.wouterbauweraerts.samples.sociablepetadoption.common.ValueOfEnum;
import jakarta.validation.constraints.NotBlank;

public record AddPetRequest(
        @NotBlank
        String name,
        @NotBlank
        @ValueOfEnum(enumClass = PetType.class)
        String type
) {
}
