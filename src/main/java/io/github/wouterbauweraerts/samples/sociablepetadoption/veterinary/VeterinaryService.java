package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary;

import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.event.PetAdoptedEvent;
import io.github.wouterbauweraerts.samples.sociablepetadoption.common.NotFoundException;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.OwnerResponse;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.OwnerService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetResponse;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.Owner;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.OwnerRepository;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.Pet;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.PetRepository;
import jakarta.transaction.Transactional;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;

@Service
public class VeterinaryService {
    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;

    private final OwnerService ownerService;
    private final PetService petService;

    private final Clock clock;

    public VeterinaryService(OwnerRepository ownerRepository, PetRepository petRepository, OwnerService ownerService, PetService petService, Clock clock) {
        this.ownerRepository = ownerRepository;
        this.petRepository = petRepository;
        this.ownerService = ownerService;
        this.petService = petService;
        this.clock = clock;
    }

    @ApplicationModuleListener
    public void onPetAdoptedEvent(PetAdoptedEvent event) {
        Owner owner = ownerRepository.findByOwnerId(event.ownerId()).orElseGet(() -> {
            OwnerResponse ownerResponse = ownerService.getOwnerById(event.ownerId())
                    .orElseThrow(() -> NotFoundException.withTypeAndId("OWNER", event.ownerId()));
            return Owner.fromOwnerResponse(ownerResponse);
        });

        owner.adoptPet(
                petService.getPet(event.petId()).map((PetResponse petResponse) -> Pet.fromPetResponse(petResponse, LocalDate.now(clock)))
                        .orElseThrow(() -> NotFoundException.withTypeAndId("PET", event.petId()))
        );

        ownerRepository.save(owner);
    }

    @Transactional
    public void registerCheckUp(Integer petId, LocalDate checkupDate) {
        petRepository.findByPetId(petId).map(
                pet -> {
                    pet.registerVetCheck(checkupDate);
                    return pet;
                }
        ).orElseThrow(() -> NotFoundException.withTypeAndId("PET", petId));
    }
}
