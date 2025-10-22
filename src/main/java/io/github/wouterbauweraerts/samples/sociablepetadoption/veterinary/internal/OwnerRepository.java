package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("vetOwnerRepository")
public interface OwnerRepository extends JpaRepository<Owner, Integer> {
    Optional<Owner> findByOwnerId(Integer ownerId);
}
