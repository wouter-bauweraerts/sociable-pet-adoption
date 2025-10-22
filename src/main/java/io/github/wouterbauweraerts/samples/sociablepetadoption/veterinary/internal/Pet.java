package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import io.github.wouterbauweraerts.samples.sociablepetadoption.common.PetType;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetResponse;
import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity(name = "VetPet")
@Table(name = "vet_pets")
public class Pet {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer internalId;

    @Column
    private Integer petId;

    @Column
    private String name;

    @Column
    private PetType type;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    public static Pet fromPetResponse(PetResponse petResponse) {
        return new Pet(null, petResponse.id(), petResponse.name(), PetType.valueOf(petResponse.type()), null);
    }

    public Pet() {
    }

    public Pet(Integer internalId, Integer petId, String name, PetType type, Owner owner) {
        this.internalId = internalId;
        this.petId = petId;
        this.name = name;
        this.type = type;
        this.owner = owner;
    }

    public Integer getInternalId() {
        return internalId;
    }

    public void setInternalId(Integer internalId) {
        this.internalId = internalId;
    }

    public Integer getPetId() {
        return petId;
    }

    public void setPetId(Integer petId) {
        this.petId = petId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PetType getType() {
        return type;
    }

    public void setType(PetType type) {
        this.type = type;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }
}
