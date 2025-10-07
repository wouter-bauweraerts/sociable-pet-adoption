package io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions;

import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.event.PetAdoptedEvent;
import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.exceptions.OwnerNotFoundException;
import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.exceptions.PetNotFoundException;
import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.request.AdoptPetCommand;
import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.internal.AdoptionMapper;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.OwnerService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.OwnerMapper;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.domain.Owner;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.domain.OwnerFixtures;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.repository.OwnerRepository;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.PetMapper;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.Pet;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetFixtures;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdoptionServiceSociableTest {
    AdoptionService adoptionService;
    PetService petService;
    OwnerService ownerService;

    @Mock
    ApplicationEventPublisher publisher;
    @Mock
    PetRepository petRepository;
    @Mock
    OwnerRepository ownerRepository;

    @Captor
    ArgumentCaptor<PetAdoptedEvent> eventCaptor;

    @BeforeEach
    void setUp() {
        petService = new PetService(
                petRepository,
                Mappers.getMapper(PetMapper.class)
        );

        ownerService = new OwnerService(
                ownerRepository,
                Mappers.getMapper(OwnerMapper.class),
                petService,
                publisher
        );

        adoptionService = new AdoptionService(
                petService,
                ownerService,
                Mappers.getMapper(AdoptionMapper.class),
                publisher
        );
    }

    @Test
    void adopt_whenPetNotAdoptable_throwsExpected() {
        AdoptPetCommand command = new AdoptPetCommand(1, 666);

        assertThatThrownBy(() -> adoptionService.adopt(command))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessage("Pet %d is not available for adoption.".formatted(command.petId()));

        verifyNoInteractions(publisher);
    }

    @Test
    void adopt_whenOwnerNotFound_throwsExpected() {
        Pet pet = PetFixtures.getAdoptablePet();
        AdoptPetCommand command = new AdoptPetCommand(1, pet.getId());

        when(petRepository.findById(command.petId())).thenReturn(Optional.of(pet));
        when(ownerRepository.findById(command.ownerId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adoptionService.adopt(command))
                .isInstanceOf(OwnerNotFoundException.class)
                .hasMessage("Owner %d is not available for adoption.".formatted(command.ownerId()));

        verifyNoInteractions(publisher);
    }

    @Test
    void adopt_whenValid_publishesExpected() {
        Pet pet = PetFixtures.getAdoptablePet();
        Owner owner = OwnerFixtures.anOwner();
        AdoptPetCommand command = new AdoptPetCommand(owner.getId(), pet.getId());

        when(petRepository.findById(command.petId())).thenReturn(Optional.of(pet));
        when(ownerRepository.findById(command.ownerId())).thenReturn(Optional.of(owner));

        assertThatCode(() -> adoptionService.adopt(command)).doesNotThrowAnyException();

        verify(publisher).publishEvent(eventCaptor.capture());

        assertThat(eventCaptor.getValue())
                .returns(command.petId(), PetAdoptedEvent::petId)
                .returns(command.ownerId(), PetAdoptedEvent::ownerId);
    }
}