package io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api;

import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.OwnerService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api.request.AddOwnerRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api.request.UpdateOwnerRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api.response.OwnerResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/owners")
public class OwnerController {
    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    public Page<OwnerResponse> listOwners(Pageable pageable) {
        return ownerService.getOwners(pageable);
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<OwnerResponse> getOwner(@PathVariable Integer ownerId) {
        return ResponseEntity.of(ownerService.getOwnerById(ownerId));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public OwnerResponse addOwner(@RequestBody @Valid AddOwnerRequest request) {
        return ownerService.addOwner(request);
    }

    @PutMapping("/{ownerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOwner(@PathVariable Integer ownerId, @RequestBody @Valid UpdateOwnerRequest request) {
        ownerService.updateOwner(ownerId, request);
    }

    @DeleteMapping("/{ownerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwner(@PathVariable Integer ownerId) {
        ownerService.deleteOwner(ownerId);
    }
}
