package io.github.wouterbauweraerts.samples.sociablepetadoption.pets;

import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.event.PetAdoptedEvent;
import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.request.AdoptablePetSearch;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.events.OwnerDeletedEvent;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api.request.AddPetRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api.response.PetResponse;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.PetMapper;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.Pet;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.repository.PetSpecification.adoptablePetSearchSpecification;
import static java.util.Optional.empty;

@Service
public class PetService {
    private static final Logger log = LoggerFactory.getLogger(PetService.class);

    private final PetRepository petRepository;
    private final PetMapper petMapper;

    public PetService(PetRepository petRepository, PetMapper petMapper) {
        this.petRepository = petRepository;
        this.petMapper = petMapper;
    }

    public Page<PetResponse> getPets(Pageable pageable) {
        return petRepository.findAll(pageable)
                .map(petMapper::map);
    }

    public Optional<PetResponse> getPet(int petId) {
        return petRepository.findById(petId)
                .map(petMapper::map);
    }

    public Page<PetResponse> getPetsAvailableForAdoption(Pageable pageable) {
        return petRepository.findPetsAvailableForAdoption(pageable)
                .map(petMapper::map);
    }

    public PetResponse addPet(AddPetRequest addPetRequest) {
        return petMapper.map(
                petRepository.save(
                        petMapper.toEnity(addPetRequest)
                )
        );
    }

    public Map<String, List<String>> getPetsForOwner(Integer ownerId) {
        return petRepository.findAllByOwnerId(ownerId)
                .stream().collect(Collectors.groupingBy(pet -> pet.getType().name(), Collectors.mapping(Pet::getName, Collectors.toList())));
    }

    public Optional<PetResponse> getPetForAdoption(Integer petId) {
        Optional<Pet> pet = petRepository.findById(petId);
        return pet.flatMap(
                p -> p.isAvailableForAdoption()
                        ? pet
                        : empty()
        ).map(petMapper::map);
    }

    @ApplicationModuleListener
    public void onOwnerDeleted(OwnerDeletedEvent ownerDeletedEvent) {
        log.info("Received OwnerDeletedEvent for owner with id {}", ownerDeletedEvent.ownerId());

        petRepository.deleteAllByOwnerId(ownerDeletedEvent.ownerId());
    }

    public Page<PetResponse> searchAdoptablePets(AdoptablePetSearch search, Pageable pageable) {
        Page<Pet> pets;
        List<PetType> types = search.getTypes().stream()
                .map(PetType::valueOf)
                .toList();

        if (types.isEmpty() && search.getNames().isEmpty()) {
            pets = petRepository.findPetsAvailableForAdoption(pageable);
        } else {
            pets = petRepository.findAll(adoptablePetSearchSpecification(types, search.getNames()), pageable);
        }

        return pets.map(petMapper::map);
    }

    @ApplicationModuleListener
    public void onPetAdopted(PetAdoptedEvent event) {
        log.info("Received PetAdoptedEvent for pet with id {} and owner with id {}", event.petId(), event.ownerId());
        petRepository.findById(event.petId()).ifPresentOrElse(
                pet -> {
                    pet.setOwnerId(event.ownerId());
                    petRepository.save(pet);
                },
                () -> {
                    throw new IllegalStateException("Pet %d not found".formatted(event.petId()));
                }
        );
    }
}
