package io.github.wouterbauweraerts.samples.sociablepetadoption.owners.internal.domain;

import io.github.wouterbauweraerts.instancio.fixture.builder.GenerateFixtureBuilder;
import io.github.wouterbauweraerts.instancio.fixture.builder.InstancioModel;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.List;

import static org.instancio.Select.allInts;
import static org.instancio.Select.field;

@GenerateFixtureBuilder(builderForType = Owner.class, fixtureClass = OwnerFixtures.class)
public class OwnerFixtures {
    @InstancioModel
    public static final Model<Owner> OWNER_MODEL = Instancio.of(Owner.class)
            .generate(allInts(), gen -> gen.ints().min(1))
            .toModel();

    public static List<Owner> getOwnersJpa(int size) {
        return Instancio.ofList(OWNER_MODEL)
                .size(size)
                .ignore(field(Owner::getId))
                .create();
    }

    public static Owner anOwnerJpa() {
        return Instancio.of(OWNER_MODEL)
                .ignore(field(Owner::getId))
                .create();
    }

    public static Owner anOwner() {
        return Instancio.create(OWNER_MODEL);
    }
}