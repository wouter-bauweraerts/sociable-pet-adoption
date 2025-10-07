package io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api;

import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.AdoptionService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.request.AdoptPetCommand;
import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.request.AdoptablePetSearch;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api.response.PetResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/adoptions")
public class AdoptionController {
    private final PetService petService;
    private final AdoptionService adoptionService;

    public AdoptionController(PetService petService, AdoptionService adoptionService) {
        this.petService = petService;
        this.adoptionService = adoptionService;
    }

    @GetMapping("/search")
    public Page<PetResponse> searchAdoptablePet(@Valid AdoptablePetSearch search, Pageable pageable) {
        return petService.searchAdoptablePets(search, pageable);
    }

    @PostMapping
    @ResponseStatus(NO_CONTENT)
    public void adoptPet(@RequestBody @Valid AdoptPetCommand command) {
        adoptionService.adopt(command);
    }
}
