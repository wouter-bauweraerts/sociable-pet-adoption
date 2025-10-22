package io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain;

import io.github.wouterbauweraerts.samples.sociablepetadoption.common.PetType;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.StringJoiner;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.isNull;

@Entity
@Table(name = "pets")
public class Pet {
    @Id
    @Column(name = "pet_id")
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;
    @Column(name = "pet_name")
    private String name;
    @Column(name = "pet_type")
    @Enumerated(STRING)
    private PetType type;
    @Column(name = "owner_id")
    private Integer ownerId;

    public Pet() {
    }

    public Pet(Integer id, String name, PetType type, Integer ownerId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.ownerId = ownerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pet pet)) return false;
        return Objects.equals(id, pet.id)
                && Objects.equals(name, pet.name)
                && type == pet.type
                && Objects.equals(ownerId, pet.ownerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, ownerId);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Pet.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("type=" + type)
                .add("ownerId=" + ownerId)
                .toString();
    }

    public boolean isAvailableForAdoption() {
        return isNull(ownerId);
    }
}
