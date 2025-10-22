package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.api;

import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.VeterinaryService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.api.request.RegisterVetCheckUpRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/veterinary")
public class VeterinaryController {
    private final VeterinaryService veterinaryService;

    public VeterinaryController(VeterinaryService veterinaryService) {
        this.veterinaryService = veterinaryService;
    }

    @PatchMapping("/checkup/{petId}")
    public void registerVetCheckUp(@PathVariable Integer petId, @RequestBody @Valid RegisterVetCheckUpRequest request) {
        veterinaryService.registerCheckUp(petId, request.checkupDate());
    }

    // Add endoint to get pricing for a veterinary check-up
    // Discount for owners based on number of pets: 1 pet = no discount, 2+ pets = 10% discount, 5+ pets = 25% discount
    // Fine based on last pet checkup: checkup within last 12 months = no fine, last checkup less than 24 months ago = 15Euro fine, last checkup more than 24 months ago = 20% fine
}
