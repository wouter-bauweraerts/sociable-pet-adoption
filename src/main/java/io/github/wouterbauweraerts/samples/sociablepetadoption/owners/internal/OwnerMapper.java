package io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal;

import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api.request.AddOwnerRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api.request.UpdateOwnerRequest;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.api.response.OwnerResponse;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.events.OwnerDeletedEvent;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.domain.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.ERROR;

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
