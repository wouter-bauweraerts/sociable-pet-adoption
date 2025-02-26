package io.github.wouterbauweraerts.samples.springpetadoption.adoptions.internal;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;

import io.github.wouterbauweraerts.samples.springpetadoption.adoptions.api.event.PetAdoptedEvent;
import io.github.wouterbauweraerts.samples.springpetadoption.adoptions.api.request.AdoptPetCommand;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = ERROR)
public interface AdoptionMapper {
    PetAdoptedEvent map(AdoptPetCommand command);
}
