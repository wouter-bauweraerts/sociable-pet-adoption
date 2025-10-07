package io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.internal;

import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.event.PetAdoptedEvent;
import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.request.AdoptPetCommand;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.ERROR;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = ERROR)
public interface AdoptionMapper {
    PetAdoptedEvent map(AdoptPetCommand command);
}
