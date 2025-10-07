package io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.repository;

import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.domain.Owner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OwnerRepositoryTest {
    @Autowired
    OwnerRepository repository;

    Owner owner1, owner2, owner3, owner4, owner5;

    // TODO Convert to test with @Sql with data from mockaroo


    @BeforeEach
    void setUp() {
        owner1 = repository.save(new Owner(null, "Wouter"));
        owner2 = repository.save(new Owner(null, "Frank"));
        owner3 = repository.save(new Owner(null, "Alina"));
        owner4 = repository.save(new Owner(null, "Josh"));
        owner5 = repository.save(new Owner(null, "Venkat"));
    }

    @Test
    void findById_returnsExpected() {
        assertThat(repository.findById(3)).hasValue(owner3);
    }
}