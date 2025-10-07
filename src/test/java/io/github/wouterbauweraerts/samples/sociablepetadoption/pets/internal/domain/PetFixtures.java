package io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain;

import io.github.wouterbauweraerts.instancio.fixture.builder.GenerateFixtureBuilder;
import io.github.wouterbauweraerts.instancio.fixture.builder.InstancioModel;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetType;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.List;

import static org.instancio.Select.allInts;
import static org.instancio.Select.field;

@GenerateFixtureBuilder(builderForType = Pet.class, fixtureClass = PetFixtures.class)
public class PetFixtures {
    @InstancioModel
    public static final Model<Pet> PET_MODEL = Instancio.of(Pet.class)
            .generate(allInts(), gen -> gen.ints().min(1))
            .generate(field(Pet::getType), gen -> gen.enumOf(PetType.class))
            .toModel();

    public static List<Pet> getPets(int size) {
        return Instancio.ofList(PET_MODEL)
                .size(size)
                .create();
    }

    public static Pet getPet() {
        return Instancio.create(PET_MODEL);
    }

    public static List<Pet> getAdoptablePets(int size) {
        return Instancio.ofList(PET_MODEL)
                .size(size)
                .ignore(field(Pet::getOwnerId))
                .create();
    }

    public static Pet getAdoptableJpaPet() {
        return Instancio.of(PET_MODEL)
                .ignore(field(Pet::getId))
                .ignore(field(Pet::getOwnerId))
                .create();
    }

    public static Pet getAdoptablePet() {
        return Instancio.of(PET_MODEL)
                .ignore(field(Pet::getOwnerId))
                .create();
    }
}