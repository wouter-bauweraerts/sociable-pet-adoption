package io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.repository;

import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.domain.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Integer> {
}
