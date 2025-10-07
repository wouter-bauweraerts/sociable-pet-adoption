package io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.repository;

import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.Pet;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class PetSpecification {
    private PetSpecification() {
        // Hide implicit default constructor for utility class
    }

    public static Specification<Pet> adoptablePetSearchSpecification(List<PetType> types, List<String> names) {
        return (root, query, cb) -> Stream.of(
                        buildNameCriteria(names, root),
                        buildTypeCriteria(types, root),
                        buildIsAdoptable(root)
                ).filter(Objects::nonNull)
                .reduce(cb::and)
                .orElse(buildIsAdoptable(root));
    }

    private static Predicate buildIsAdoptable(Root<Pet> root) {
        return root.get("ownerId").isNull();
    }

    private static Predicate buildTypeCriteria(List<PetType> types, Root<Pet> root) {
        if (types.isEmpty()) {
            return null;
        }

        return root.get("type").in(types);
    }

    private static Predicate buildNameCriteria(List<String> names, Root<Pet> root) {
        if (names.isEmpty()) {
            return null;
        }

        return root.get("name").as(String.class).in(names);
    }
}
