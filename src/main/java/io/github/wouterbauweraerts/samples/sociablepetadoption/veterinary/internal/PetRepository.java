package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("vetPetRepository")
public interface PetRepository extends JpaRepository<Pet, Integer> {
    Optional<Pet> findByPetId(Integer petId);
}
