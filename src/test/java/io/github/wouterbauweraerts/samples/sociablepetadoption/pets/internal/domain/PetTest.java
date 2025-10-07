package io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain;

import org.junit.jupiter.api.Test;

import static io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetType.DOG;
import static org.assertj.core.api.Assertions.assertThat;

class PetTest {
    @Test
    void petWithOwnerIsNotAvailableForAdoption() {
        Pet pet = new Pet(1, "Roxy", DOG, 12);

        assertThat(pet.isAvailableForAdoption()).isFalse();
    }

    @Test
    void petWithoutOwnerIsNotAvailableForAdoption() {
        Pet pet = new Pet(1, "Roxy", DOG, null);

        assertThat(pet.isAvailableForAdoption()).isTrue();
    }
}