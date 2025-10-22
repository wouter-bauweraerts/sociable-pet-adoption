package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary;

import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.event.PetAdoptedEvent;
import io.github.wouterbauweraerts.samples.sociablepetadoption.common.NotFoundException;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.OwnerResponse;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.OwnerService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.Owner;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.OwnerRepository;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.Pet;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
public class VeterinaryService {
    private final OwnerRepository ownerRepository;

    private final OwnerService ownerService;
    private final PetService petService;

    public VeterinaryService(OwnerRepository ownerRepository, OwnerService ownerService, PetService petService) {
        this.ownerRepository = ownerRepository;
        this.ownerService = ownerService;
        this.petService = petService;
    }

    @ApplicationModuleListener
    public void onPetAdoptedEvent(PetAdoptedEvent event) {
        Owner owner = ownerRepository.findByOwnerId(event.ownerId()).orElseGet(() -> {
            OwnerResponse ownerResponse = ownerService.getOwnerById(event.ownerId())
                    .orElseThrow(() -> NotFoundException.withTypeAndId("OWNER", event.ownerId()));
            return Owner.fromOwnerResponse(ownerResponse);
        });

        owner.addPet(
                petService.getPet(event.petId()).map(Pet::fromPetResponse)
                        .orElseThrow(() -> NotFoundException.withTypeAndId("PET", event.petId()))
        );

        ownerRepository.save(owner);
    }
}
