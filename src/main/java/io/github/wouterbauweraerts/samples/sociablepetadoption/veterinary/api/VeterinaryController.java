package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.api;

import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.VeterinaryService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.api.request.RegisterVetCheckUpRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.api.response.VetCheckupPriceResponse;
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

    @GetMapping("/{ownerId}/checkup/{petId}")
    public VetCheckupPriceResponse getCheckupPrice(@PathVariable Integer ownerId, @PathVariable Integer petId) {
        return veterinaryService.getCheckupPrice(ownerId, petId);
    }
}
