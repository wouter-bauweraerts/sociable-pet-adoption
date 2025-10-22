package io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal;

import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetResponse;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api.request.AddPetRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.Pet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public abstract class PetMapper {
    public abstract PetResponse map(Pet pet);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    public abstract Pet toEnity(AddPetRequest request);
}
