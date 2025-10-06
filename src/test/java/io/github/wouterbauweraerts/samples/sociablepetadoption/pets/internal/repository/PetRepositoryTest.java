package io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.repository;

import static io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetType.CAT;
import static io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetType.DOG;
import static io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetType.GOAT;
import static io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetType.HAMSTER;
import static io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetType.TURTLE;
import static io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.repository.PetSpecification.adoptablePetSearchSpecification;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;

import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.Pet;

@DataJpaTest
class PetRepositoryTest {
    @Autowired
    PetRepository petRepository;

    Pet pet1, pet2, pet3, pet4, pet5, pet6, pet7, pet8, pet9, pet10;

    @BeforeEach
    void setUp() {
        pet1 = petRepository.save(new Pet(null, "Roxy", DOG, 1));
        pet2 = petRepository.save(new Pet(null, "Rex", DOG, null));
        pet3 = petRepository.save(new Pet(null, "Filou", CAT, 3));
        pet4 = petRepository.save(new Pet(null, "Bella", CAT, 3));
        pet5 = petRepository.save(new Pet(null, "Luna", HAMSTER, null));
        pet6 = petRepository.save(new Pet(null, "Hammy", HAMSTER, 3));
        pet7 = petRepository.save(new Pet(null, "Michelangelo", TURTLE, null));
        pet8 = petRepository.save(new Pet(null, "Donatello", TURTLE, null));
        pet9 = petRepository.save(new Pet(null, "Leonardo", GOAT, null));
        pet10 = petRepository.save(new Pet(null, "Raphael", GOAT, null));
    }

    @AfterEach
    void tearDown() {
        petRepository.deleteAll();
    }

    @Test
    void findAll_unpaged_returnsAll() {
        assertThat(petRepository.findAll(Pageable.unpaged()))
                .containsExactlyInAnyOrder(
                        pet1, pet2, pet3, pet4, pet5, pet6, pet7, pet8, pet9, pet10
                );
    }

    @TestFactory
    Stream<DynamicTest> findAll_withPagination_returnsExpected() {
        return Stream.of(
                Pair.of(
                        PageRequest.of(0, 2),
                        List.of(pet1, pet2)
                ),
                Pair.of(
                        PageRequest.of(0, 5),
                        List.of(pet1, pet2, pet3, pet4, pet5)
                ),
                Pair.of(
                        PageRequest.of(0, 20),
                        List.of(pet1, pet2, pet3, pet4, pet5, pet6, pet7, pet8, pet9, pet10)
                ),
                Pair.of(
                        PageRequest.of(3, 2),
                        List.of(pet7, pet8)
                )
        ).map(pair -> dynamicTest(
                "findAll with pagination params %s returns expected elements %s"
                        .formatted(pair.getFirst(), pair.getSecond().stream().map(Pet::getId).toList()),
                () -> assertThat(petRepository.findAll(pair.getFirst())).containsExactlyInAnyOrderElementsOf(pair.getSecond())
        ));
    }

    @TestFactory
    Stream<DynamicTest> findById_returnsExpected() {
        return Stream.of(
                pet1, pet2, pet3, pet4, pet5, pet6
        ).map(p -> dynamicTest(
                "findById with id %d returns %s".formatted(p.getId(), p),
                () -> assertThat(petRepository.findById(p.getId())).hasValue(p)
        ));
    }

    @Test
    void findById_withNonExistingId_returnsEmpty() {
        assertThat(petRepository.findById(-1)).isEmpty();
    }

    @TestFactory
    Stream<DynamicTest> findPetsAvailableForAdoption_returnsExpected() {
        return Stream.of(
                Pair.of(
                        PageRequest.of(0, 2),
                        List.of(pet2, pet5)
                ),
                Pair.of(
                        PageRequest.of(0, 5),
                        List.of(pet2, pet5, pet7, pet8, pet9)
                ),
                Pair.of(
                        PageRequest.of(0, 20),
                        List.of(pet2, pet5, pet7, pet8, pet9, pet10)
                ),
                Pair.of(
                        PageRequest.of(2, 2),
                        List.of(pet9, pet10)
                )
        ).map(pair -> dynamicTest(
                "findPetsAvailableForAdoption with pagination params %s returns expected elements %s"
                        .formatted(pair.getFirst(), pair.getSecond().stream().map(Pet::getId).toList()),
                () -> assertThat(petRepository.findPetsAvailableForAdoption(pair.getFirst())).containsExactlyInAnyOrderElementsOf(pair.getSecond())
        ));
    }

    @Test
    void findAllByOwnerId_returnsExpectedPets() {
        assertThat(petRepository.findAllByOwnerId(3)).containsExactlyInAnyOrder(
                pet3, pet4, pet6
        );
    }

    @Test
    void deleteAllByOwnerId_deletesExpectedPets() {
        assertThat(petRepository.findAll()).contains(pet3, pet4, pet6);
        petRepository.deleteAllByOwnerId(3);
        assertThat(petRepository.findAll()).doesNotContain(pet3, pet4, pet6);
    }

    @Test
    void findAll_pagedWithSpecification_noSearchParams_returnsExpected() {
        Specification<Pet> spec = adoptablePetSearchSpecification(List.of(), List.of());

        assertThat(petRepository.findAll(spec, PageRequest.of(0, 20)))
                .containsExactlyInAnyOrder(
                        pet2, pet5, pet7, pet8, pet9, pet10
                );
    }

    @Test
    void findAll_pagedWithSpecification_noSearchParams_paginated_returnsExpected() {
        Specification<Pet> spec = adoptablePetSearchSpecification(List.of(), List.of());

        assertThat(petRepository.findAll(spec, PageRequest.of(0, 2)))
                .containsExactlyInAnyOrder(
                        pet2, pet5
                );
    }

    @Test
    void findAll_pagedWithSpecification_namesOnly_returnsExpected() {
        Specification<Pet> spec = adoptablePetSearchSpecification(
                List.of(),
                List.of("Roxy", "Rex", "Luna", "Donatello", "Frank", "Frey")
        );

        assertThat(petRepository.findAll(spec, PageRequest.of(0, 20)))
                .containsExactlyInAnyOrder(
                        pet2, pet5, pet8
                );
    }

    @Test
    void findAll_pagedWithSpecification_namesAndTypes_returnsExpected() {
        Specification<Pet> spec = adoptablePetSearchSpecification(
                List.of(DOG, HAMSTER),
                List.of("Roxy", "Rex", "Luna", "Donatello", "Frank", "Frey")
        );

        assertThat(petRepository.findAll(spec, PageRequest.of(0, 20)))
                .containsExactlyInAnyOrder(
                        pet2, pet5
                );
    }

    @Test
    void findAll_pagedWithSpecification_typesOnly_returnsExpected() {
        Specification<Pet> spec = adoptablePetSearchSpecification(
                List.of(DOG, GOAT),
                List.of()
        );

        assertThat(petRepository.findAll(spec, PageRequest.of(0, 20)))
                .containsExactlyInAnyOrder(
                        pet2, pet9, pet10
                );
    }
}