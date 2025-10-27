package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import io.github.wouterbauweraerts.samples.sociablepetadoption.common.PetType;
import org.instancio.Instancio;
import org.instancio.Model;

import java.time.LocalDate;

import static org.instancio.Select.allInts;
import static org.instancio.Select.field;

class VetPetFixtures {
    public static final Model<Pet> VET_PET = Instancio.of(Pet.class)
            .generate(allInts(), gen -> gen.ints().min(10).max(1000))
            .generate(field(Pet::getType), gen -> gen.enumOf(PetType.class))
            .generate(field(Pet::getLastVetCheck), gen -> gen.temporal().localDate().range(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 9, 1)))
            .toModel();

    public static Pet aPetWithLastCheckupDate(LocalDate lastCheckupDate) {
        return Instancio.of(VET_PET)
                .set(field(Pet::getLastVetCheck), lastCheckupDate)
                .create();
    }
}