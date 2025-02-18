package io.github.wouterbauweraerts.samples.springpetadoption.owners.internal;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import io.github.wouterbauweraerts.samples.springpetadoption.owners.OwnerDeletedEvent;
import io.github.wouterbauweraerts.samples.springpetadoption.owners.api.request.AddOwnerRequest;
import io.github.wouterbauweraerts.samples.springpetadoption.owners.api.request.UpdateOwnerRequest;
import io.github.wouterbauweraerts.samples.springpetadoption.owners.api.response.OwnerResponse;
import io.github.wouterbauweraerts.samples.springpetadoption.owners.internal.domain.Owner;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = ERROR)
public interface OwnerMapper {
    @Mapping(target = "pets", expression = "java(java.util.Map.of())")
    OwnerResponse map(Owner owner);

    @Mapping(target = "id", ignore = true)
    Owner toEntity(AddOwnerRequest request);

    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget Owner owner, UpdateOwnerRequest request);

    OwnerDeletedEvent ownerDeleted(Integer ownerId);
}
