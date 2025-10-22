package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record RegisterVetCheckUpRequest(
        @NotNull
        @PastOrPresent
        LocalDate checkupDate
) {
}
