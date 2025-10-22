package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary;

import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.event.PetAdoptedEvent;
import io.github.wouterbauweraerts.samples.sociablepetadoption.common.PetType;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.OwnerResponse;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.OwnerService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api.request.AddOwnerRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetResponse;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api.request.AddPetRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.Owner;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.OwnerRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@ApplicationModuleTest(mode = ApplicationModuleTest.BootstrapMode.DIRECT_DEPENDENCIES)
class VeterinaryServicePetAdoptedTest {
    @Autowired
    PetService petService;
    @Autowired
    OwnerService ownerService;

    @Autowired
    OwnerRepository ownerRepository;

    @AfterEach
    void tearDown() {
        ownerRepository.deleteAll();
    }

    @Test
    @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "cleaup.sql")
    void onPetAdoptedEvent_createsExpectedEntities(Scenario scenario) {
        OwnerResponse wouter = ownerService.addOwner(new AddOwnerRequest("Wouter"));
        PetResponse roxy = petService.addPet(new AddPetRequest("Roxy", "DOG"));

        scenario.publish(new PetAdoptedEvent(wouter.getId(), roxy.id()))
                .andWaitAtMost(Duration.of(10, ChronoUnit.SECONDS))
                .andWaitForStateChange(() -> ownerRepository.findByOwnerId(wouter.getId()))
                .andVerify(optOwner -> {
                    Optional<Owner> persisted = ownerRepository.findByOwnerId(wouter.getId());
                    assertThat(persisted).isNotEmpty();
                    Owner owner = persisted.get();
                    SoftAssertions.assertSoftly(softly -> {
                        softly.assertThat(owner.getInternalId()).isNotNull();
                        softly.assertThat(owner.getOwnerId()).isEqualTo(wouter.getId());
                        softly.assertThat(owner.getName()).isEqualTo(wouter.getName());
                        softly.assertThat(owner.getPets()).hasSize(1)
                                .allSatisfy(pet -> {
                                    softly.assertThat(pet.getInternalId()).isNotNull();
                                    softly.assertThat(pet.getPetId()).isEqualTo(roxy.id());
                                    softly.assertThat(pet.getName()).isEqualTo(roxy.name());
                                    softly.assertThat(pet.getType()).isEqualTo(PetType.valueOf(roxy.type()));
                                });
                    });
                });
    }
}