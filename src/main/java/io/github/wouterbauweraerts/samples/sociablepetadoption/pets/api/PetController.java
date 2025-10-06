package io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api;

import static org.springframework.http.HttpStatus.CREATED;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api.request.AddPetRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api.response.PetResponse;
import jakarta.validation.Valid;

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
