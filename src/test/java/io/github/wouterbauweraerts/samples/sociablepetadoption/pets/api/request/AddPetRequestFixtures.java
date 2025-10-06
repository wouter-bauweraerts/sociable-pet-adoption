package io.github.wouterbauweraerts.samples.sociablepetadoption.pets.api.request;

import io.github.wouterbauweraerts.instancio.fixture.builder.GenerateFixtureBuilder;
import io.github.wouterbauweraerts.instancio.fixture.builder.InstancioModel;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.internal.domain.PetType;
import org.instancio.Instancio;
import org.instancio.Model;

import static org.instancio.Select.field;

@GenerateFixtureBuilder(builderForType = AddPetRequest.class, fixtureClass = AddPetRequestFixtures.class)
public class AddPetRequestFixtures {
    @InstancioModel
    public static final Model<AddPetRequest> ADD_PET_REQUEST_MODEL = Instancio.of(AddPetRequest.class)
            .generate(field(AddPetRequest::type), gen -> gen.enumOf(PetType.class).as(Enum::name))
            .toModel();

    public static AddPetRequest anAddPetRequest() {
        return Instancio.create(ADD_PET_REQUEST_MODEL);
    }
}