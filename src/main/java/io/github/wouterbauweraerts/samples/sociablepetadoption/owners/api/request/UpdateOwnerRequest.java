package io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateOwnerRequest(@NotBlank String name) {
}
