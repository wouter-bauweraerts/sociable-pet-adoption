package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.OwnerResponse;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity(name = "VetOwner")
@Table(name = "vet_owners")
public class Owner {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer internalId;

    @Column(unique = true)
    private Integer ownerId;

    @Column(name = "name")
    private String name;

    @OneToMany(fetch = EAGER, cascade = CascadeType.ALL)
    private List<Pet> pets;

    public Owner() {
    }

    public Owner(Integer internalId, Integer ownerId, String name, List<Pet> pets) {
        this.internalId = internalId;
        this.ownerId = ownerId;
        this.name = name;
        this.pets = pets;
    }

    public static Owner fromOwnerResponse(OwnerResponse owner) {
        return new Owner(null, owner.getId(), owner.getName(), new ArrayList<>());
    }

    public Integer getInternalId() {
        return internalId;
    }

    public void setInternalId(Integer internalId) {
        this.internalId = internalId;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

    public void addPet(Pet pet) {
        this.pets.add(pet);
        pet.setOwner(this);
    }
}
