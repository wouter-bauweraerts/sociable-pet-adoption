package io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AdoptPetCommand(
        @NotNull @PositiveOrZero Integer ownerId,
        @NotNull @PositiveOrZero Integer petId
) {
}
