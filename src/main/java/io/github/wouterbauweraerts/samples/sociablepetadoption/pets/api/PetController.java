package io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api;

import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetResponse;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api.request.AddPetRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/pets")
public class PetController {
    private PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public Page<PetResponse> listPets(Pageable page) {
        return petService.getPets(page);
    }

    @GetMapping("/{petId}")
    public ResponseEntity<PetResponse> getPet(@PathVariable Integer petId) {
        return ResponseEntity.of(petService.getPet(petId));
    }

    @GetMapping("/available-for-adoption")
    public Page<PetResponse> findAvailablePets(Pageable page) {
        return petService.getPetsAvailableForAdoption(page);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public PetResponse addPet(@RequestBody @Valid AddPetRequest addPetRequest) {
        return petService.addPet(addPetRequest);
    }
}
