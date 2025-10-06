package io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Integer>, JpaSpecificationExecutor<Pet> {
    @Query("SELECT p FROM Pet p WHERE p.ownerId IS NULL")
    Page<Pet> findPetsAvailableForAdoption(Pageable pageable);

    List<Pet> findAllByOwnerId(Integer ownerId);

    @Modifying
    @Query("DELETE FROM Pet p WHERE p.ownerId = :ownerId")
    void deleteAllByOwnerId(Integer ownerId);

}
