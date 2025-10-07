package io.github.wouterbauweraerts.samples.sociablepetadoption.pets;

import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.event.PetAdoptedEvent;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.events.OwnerDeletedEvent;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api.request.AddPetRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api.response.PetResponse;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.PetMapper;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.PetMapperImpl;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.Pet;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.repository.PetRepository;
import io.github.wouterbauweraerts.unitsocializer.core.annotations.InjectTestInstance;
import io.github.wouterbauweraerts.unitsocializer.core.annotations.Resolve;
import io.github.wouterbauweraerts.unitsocializer.core.annotations.TestSubject;
import io.github.wouterbauweraerts.unitsocializer.junit.mockito.annotations.SociableTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetType.CAT;
import static io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetType.DOG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SociableTest
class PetServiceUnitSocializerTest {
    @TestSubject(
            typeResolvers = {
                    @Resolve(forClass = PetMapper.class, use = PetMapperImpl.class)
            }
    )
    PetService petService;

    @InjectTestInstance
    PetRepository petRepository;

    @Test
    void getPets_executesPaginatesQueryAndReturnsExpected() {
        Pageable pageRequest = mock(Pageable.class);
        List<Pet> petEntities = List.of(
                new Pet(1, "Roxy", DOG, 1),
                new Pet(2, "Rex", DOG, null),
                new Pet(3, "Filou", CAT, null)
        );

        List<PetResponse> petDtos = List.of(
                new PetResponse(1, "Roxy", DOG.name()),
                new PetResponse(2, "Rex", DOG.name()),
                new PetResponse(3, "Filou", CAT.name())
        );

        when(petRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(petEntities));

        assertThat(petService.getPets(pageRequest)).containsExactlyElementsOf(petDtos);
    }

    @Test
    void getPet_findsPetByIdAndReturnsExpected() {
        Pet petEntity = new Pet(1, "Roxy", DOG, 1);
        PetResponse petDto = new PetResponse(1, "Roxy", DOG.name());

        when(petRepository.findById(anyInt())).thenReturn(Optional.of(petEntity));

        assertThat(petService.getPet(petEntity.getId())).hasValue(petDto);
    }

    @Test
    void getPetWhenNothingFound_returnsEmpty() {
        when(petRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThat(petService.getPet(1)).isEmpty();
    }

    @Test
    void getPetsAvailableForAdoption_returnsExpected() {
        List<Pet> petEntities = List.of(
                new Pet(2, "Rex", DOG, null),
                new Pet(3, "Filou", CAT, null)
        );

        List<PetResponse> petDtos = List.of(
                new PetResponse(2, "Rex", DOG.name()),
                new PetResponse(3, "Filou", CAT.name())
        );

        when(petRepository.findPetsAvailableForAdoption(any(Pageable.class)))
                .thenReturn(new PageImpl<>(petEntities));

        assertThat(petService.getPetsAvailableForAdoption(mock(Pageable.class))).containsExactlyElementsOf(petDtos);
    }

    @Test
    void addPetCreatedNewPetAndPersist() {
        AddPetRequest addPetRequest = new AddPetRequest("Goofy", "DOG");
        Pet newPet = new Pet(null, "Goofy", DOG, null);
        Pet persistedPet = new Pet(42, "Goofy", DOG, null);
        PetResponse expectedResponse = new PetResponse(42, "Goofy", "DOG");

        when(petRepository.save(any(Pet.class))).thenReturn(persistedPet);

        assertThat(petService.addPet(addPetRequest)).isEqualTo(expectedResponse);

        verify(petRepository).save(newPet);
    }

    @Test
    void getPetsForOwner_noPets_returnsEmptyMap() {
        when(petRepository.findAllByOwnerId(anyInt())).thenReturn(List.of());

        assertThat(petService.getPetsForOwner(1)).isEmpty();
    }

    @Test
    void getPetsForOwner_returnsExpectedMap() {
        Pet roxy = new Pet(1, "Roxy", DOG, 1);
        Pet rex = new Pet(2, "Rex", DOG, 1);
        Pet filou = new Pet(3, "Filou", CAT, 1);

        List<Pet> pets = List.of(roxy, rex, filou);
        when(petRepository.findAllByOwnerId(anyInt())).thenReturn(pets);

        assertThat(petService.getPetsForOwner(1)).containsExactlyInAnyOrderEntriesOf(
                Map.of(
                        "DOG", List.of("Roxy", "Rex"),
                        "CAT", List.of("Filou")
                )
        );
    }

    @Test
    void onOwnerDeletedAllPetsOfOwnerAreDeleted() {
        OwnerDeletedEvent ownerDeletedEvent = new OwnerDeletedEvent(13);

        petService.onOwnerDeleted(ownerDeletedEvent);

        verify(petRepository).deleteAllByOwnerId(ownerDeletedEvent.ownerId());
    }

    @ParameterizedTest
    @MethodSource("noAdoptablePetSource")
    void getPetForAdoption_returnsEmpty(Optional<Pet> optionalPet) {

        when(petRepository.findById(anyInt())).thenReturn(optionalPet);
        assertThat(petService.getPetForAdoption(666)).isEmpty();
    }

    private static Stream<Arguments> noAdoptablePetSource() {
        return Stream.of(
                Arguments.of(Optional.empty()),
                Arguments.of(Optional.of(new Pet(1, "Roxy", DOG, 1)))
        );
    }

    @Test
    void getPetForAdoption_returnsExpected() {
        Pet petEntity = new Pet(1, "Vasco", DOG, null);
        PetResponse petDto = new PetResponse(1, "Vasco", DOG.name());

        when(petRepository.findById(anyInt())).thenReturn(Optional.of(petEntity));

        assertThat(petService.getPetForAdoption(petEntity.getId())).hasValue(petDto);
    }

    @Test
    void onPetAdoptedEvent_whenPetNotFound_throwsExpected() {
        PetAdoptedEvent event = new PetAdoptedEvent(1, 666);

        when(petRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.onPetAdopted(event))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Pet %d not found".formatted(event.petId()));
    }

    @Test
    void onPetAdoptedEvent_setsTheOwnerFromAPet() {
        PetAdoptedEvent event = new PetAdoptedEvent(1, 666);
        Pet unadoptedPet = new Pet(666, "Vasco", DOG, null);
        Pet adoptedPet = new Pet(666, "Vasco", DOG, 1);

        when(petRepository.findById(anyInt())).thenReturn(Optional.of(unadoptedPet));

        petService.onPetAdopted(event);

        verify(petRepository).save(adoptedPet);
    }
}