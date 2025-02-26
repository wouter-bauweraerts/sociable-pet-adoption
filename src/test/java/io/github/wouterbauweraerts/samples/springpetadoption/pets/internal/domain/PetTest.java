package io.github.wouterbauweraerts.samples.springpetadoption.pets.internal.domain;

import static io.github.wouterbauweraerts.samples.springpetadoption.pets.internal.domain.PetType.DOG;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

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