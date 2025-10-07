package io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.request;

import io.github.wouterbauweraerts.samples.sociablepetadoption.common.ValueOfEnum;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetType;

import java.util.List;

public class AdoptablePetSearch {
    private List<@ValueOfEnum(enumClass = PetType.class) String> types = List.of();
    private List<String> names = List.of();

    public AdoptablePetSearch() {
    }

    public AdoptablePetSearch(List<String> types, List<String> names) {
        this.types = types;
        this.names = names;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }
}
