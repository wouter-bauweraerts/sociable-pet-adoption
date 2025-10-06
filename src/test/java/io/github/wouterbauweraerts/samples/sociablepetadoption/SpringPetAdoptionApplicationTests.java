package io.github.wouterbauweraerts.samples.sociablepetadoption;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SpringPetAdoptionApplicationTests {

    @Test
    void moduleCheck() {
        ApplicationModules modules = ApplicationModules.of(SpringPetAdoptionApplication.class);
        modules.verify();

        new Documenter(modules)
                .writeDocumentation()
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }

}
