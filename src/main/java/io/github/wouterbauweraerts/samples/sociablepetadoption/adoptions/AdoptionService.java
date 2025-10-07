package io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions;

import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.exceptions.OwnerNotFoundException;
import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.exceptions.PetNotFoundException;
import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.request.AdoptPetCommand;
import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.internal.AdoptionMapper;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.OwnerService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetService;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AdoptionService {
    private final PetService petService;
    private final OwnerService ownerService;
    private final AdoptionMapper adoptionMapper;
    private final ApplicationEventPublisher publisher;

    public AdoptionService(PetService petService, OwnerService ownerService, AdoptionMapper adoptionMapper, ApplicationEventPublisher publisher) {
        this.petService = petService;
        this.ownerService = ownerService;
        this.adoptionMapper = adoptionMapper;
        this.publisher = publisher;
    }

    @Transactional
    public void adopt(AdoptPetCommand command) {
        validateAdoption(command.ownerId(), command.petId());

        publisher.publishEvent(adoptionMapper.map(command));
    }

    private void validateAdoption(Integer ownerId, Integer petId) {
        petService.getPetForAdoption(petId).orElseThrow(() -> PetNotFoundException.withId(petId));
        ownerService.getOwnerById(ownerId).orElseThrow(() -> OwnerNotFoundException.withId(ownerId));
    }
}
