package io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.event;

public record PetAdoptedEvent(
        Integer ownerId,
        Integer petId
) {
}
