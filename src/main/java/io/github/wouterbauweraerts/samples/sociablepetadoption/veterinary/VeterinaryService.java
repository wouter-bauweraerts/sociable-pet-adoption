package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary;

import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.event.PetAdoptedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
public class VeterinaryService {
    @ApplicationModuleListener
    public void onPetAdoptedEvent(PetAdoptedEvent event) {

    }
}
