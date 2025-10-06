package io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.domain;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "owners")
public class Owner {
    @Id
    @Column(name = "owner_id")
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;

    public Owner() {
    }

    public Owner(Integer id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Owner owner)) return false;
        return Objects.equals(id, owner.id) && Objects.equals(name, owner.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
