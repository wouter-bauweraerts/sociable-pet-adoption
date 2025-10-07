package io.github.wouterbauweraerts.samples.sociablepetadoption.owners;

import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api.request.AddOwnerRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api.request.UpdateOwnerRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api.response.OwnerResponse;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.events.OwnerDeletedEvent;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.OwnerMapper;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.OwnerMapperImpl;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.domain.Owner;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.repository.OwnerRepository;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.PetMapper;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.PetMapperImpl;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetFixtureBuilder;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetType;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.repository.PetRepository;
import io.github.wouterbauweraerts.unitsocializer.core.annotations.InjectTestInstance;
import io.github.wouterbauweraerts.unitsocializer.core.annotations.Resolve;
import io.github.wouterbauweraerts.unitsocializer.core.annotations.TestSubject;
import io.github.wouterbauweraerts.unitsocializer.junit.mockito.annotations.SociableTest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SociableTest
class OwnerServiceUnitSocializerTest {
    @TestSubject(
            typeResolvers = {
                    @Resolve(forClass = OwnerMapper.class, use = OwnerMapperImpl.class),
                    @Resolve(forClass = PetMapper.class, use = PetMapperImpl.class)
            }
    )
    OwnerService ownerService;

    @InjectTestInstance
    OwnerRepository ownerRepository;
    @InjectTestInstance
    PetRepository petRepository;
    @InjectTestInstance
    ApplicationEventPublisher eventPublisher;

    @Test
    void getOwners_returnsExpected() {
        Pageable pageable = mock(Pageable.class);
        List<Owner> owners = List.of(
                new Owner(1, "Wouter"),
                new Owner(2, "Alina"),
                new Owner(3, "Josh"),
                new Owner(4, "Matthias")
        );

        List<OwnerResponse> expected = List.of(
                new OwnerResponse(1, "Wouter", Map.of("DOG", List.of("Roxy"))),
                new OwnerResponse(2, "Alina", Map.of()),
                new OwnerResponse(3, "Josh", Map.of("HAMSTER", List.of("Production"))),
                new OwnerResponse(4, "Matthias", Map.of())
        );

        when(ownerRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(owners));

        when(petRepository.findAllByOwnerId(anyInt()))
                .thenAnswer(invocation -> switch ((int) invocation.getArgument(0)) {
                            case 1 -> List.of(PetFixtureBuilder.fixtureBuilder().withName("Roxy").withType(PetType.DOG).build());
                            case 3 -> List.of(PetFixtureBuilder.fixtureBuilder().withName("Production").withType(PetType.HAMSTER).build());
                            default -> List.of();
                        }
                );
        assertThat(ownerService.getOwners(pageable)).containsExactlyInAnyOrderElementsOf(expected);
        verify(ownerRepository).findAll(pageable);
    }

    @Test
    void getOwnerById_notFoundReturnsEmpty() {
        when(ownerRepository.findById(any())).thenReturn(Optional.empty());

        assertThat(ownerService.getOwnerById(1)).isEmpty();
    }

//    @Test
//    void getOwnerById_returnsExpected() {
//        Owner wouter = new Owner(1, "Wouter");
//        OwnerResponse expected = new OwnerResponse(1, "Wouter", Map.of("DOG", List.of("Roxy")));
//
//        when(ownerRepository.findById(any())).thenReturn(Optional.of(wouter));
//        when(petRepository.getPetsForOwner(eq(1))).thenReturn(Map.of("DOG", List.of("Roxy")));
//
//        assertThat(ownerService.getOwnerById(1)).hasValue(expected);
//        verify(ownerRepository).findById(1);
//    }

    @Test
    void addOwner_returnsExpected() {
        AddOwnerRequest request = new AddOwnerRequest("Mario");
        Owner unpersistedOwner = new Owner(null, "Mario");
        Owner persistedOwner = new Owner(13, "Mario");
        OwnerResponse expected = new OwnerResponse(13, "Mario", Map.of());

        when(ownerRepository.save(any(Owner.class))).thenReturn(persistedOwner);

        assertThat(ownerService.addOwner(request)).isEqualTo(expected);

        verify(ownerRepository).save(unpersistedOwner);
    }

    @Test
    void updateOwner_notFound() {
        UpdateOwnerRequest request = new UpdateOwnerRequest("Maria");

        when(ownerRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ownerService.updateOwner(13, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Owner with id 13 not found");
    }

    @Test
    void updateOwner_updatesExistingAndSaves() {
        Owner original = new Owner(13, "Mario");
        UpdateOwnerRequest request = new UpdateOwnerRequest("Maria");
        Owner updatedOwner = new Owner(13, "Maria");

        when(ownerRepository.findById(anyInt())).thenReturn(Optional.of(original));

        ownerService.updateOwner(13, request);

        assertThat(original.getName()).isEqualTo(request.name());
        verify(ownerRepository).save(updatedOwner);
    }

    @Test
    void deleteOwner_doesNotExist_nothingHappens() {
        when(ownerRepository.existsById(anyInt())).thenReturn(false);

        ownerService.deleteOwner(13);

        verify(ownerRepository).existsById(13);
        verifyNoMoreInteractions(ownerRepository);
        verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    void deleteOwner_deletesFromRepositoryAndDispatchesEvent() {
        when(ownerRepository.existsById(anyInt())).thenReturn(true);

        ownerService.deleteOwner(13);

        verify(ownerRepository).existsById(13);
        verify(ownerRepository).deleteById(13);
        verify(eventPublisher).publishEvent(new OwnerDeletedEvent(13));
    }
}