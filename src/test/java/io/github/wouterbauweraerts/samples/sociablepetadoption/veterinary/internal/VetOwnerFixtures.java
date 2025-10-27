package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import org.instancio.Instancio;
import org.instancio.Model;

import java.time.LocalDate;
import java.util.ArrayList;

import static io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.VetPetFixtures.VET_PET;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.*;

public class VetOwnerFixtures {
    public static final Model<Owner> VET_OWNER = Instancio.of(Owner.class)
            .generate(allInts(), gen -> gen.ints().min(10).max(1000))
            .setModel(field(Owner::getPets), Instancio.ofList(VET_PET).toModel())
            .assign(valueOf(root()).to(Pet::getOwner))
            .toModel();

    public static Owner anOwnerWithoutPets() {
        return Instancio.of(VET_OWNER)
                .set(field(Owner::getPets), new ArrayList<>())
                .create();
    }

    public static Owner anOwnerWithPetsNoFines() {
        return Instancio.create(VET_OWNER);
    }

    public static Owner anOwnerWithPetsNoFines(int numberOfPets) {
        return Instancio.of(VET_OWNER)
                .set(field(Owner::getPets), Instancio.ofList(VET_PET)
                        .size(numberOfPets)
                        .create()
                ).create();
    }

    public static Owner anOwnerWithPetsNoFines(int numberOfPets, LocalDate lastCheckup) {
        return Instancio.of(VET_OWNER)
                .set(field(Owner::getPets), Instancio.ofList(VET_PET)
                        .size(numberOfPets)
                        .set(field(Pet::getLastVetCheck), lastCheckup)
                        .create()
                ).create();
    }
}