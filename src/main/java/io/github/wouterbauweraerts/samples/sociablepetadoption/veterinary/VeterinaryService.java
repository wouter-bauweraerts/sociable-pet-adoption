package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary;

import io.github.wouterbauweraerts.samples.sociablepetadoption.adoptions.api.event.PetAdoptedEvent;
import io.github.wouterbauweraerts.samples.sociablepetadoption.common.NotFoundException;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.OwnerResponse;
import io.github.wouterbauweraerts.samples.sociablepetadoption.owners.OwnerService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetResponse;
import io.github.wouterbauweraerts.samples.sociablepetadoption.pets.PetService;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.api.response.VetCheckupPriceResponse;
import io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.*;
import jakarta.transaction.Transactional;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;

@Service
public class VeterinaryService {
    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;

    private final OwnerService ownerService;
    private final PetService petService;

    private final Clock clock;
    private final VeterinaryCheckUpConfigProperties checkupConfiguration;

    /* TODO demo:
        Uncomment CheckupPriceCalculator
        Add constructor parameter
        Bind constructor parameter to field
        Replace implementation with method call to checkupPriceCalculator
        Checkout and run tests: GetCheckupPriceTest, GetCheckupPriceSociableTest, GetCheckupPriceUnitSocializerTest
     */

//    private final CheckupPriceCalculator checkupPriceCalculator;

    public VeterinaryService(OwnerRepository ownerRepository,
                             PetRepository petRepository,
                             OwnerService ownerService,
                             PetService petService,
                             Clock clock,
                             VeterinaryCheckUpConfigProperties checkupConfiguration
    ) {
        this.ownerRepository = ownerRepository;
        this.petRepository = petRepository;
        this.ownerService = ownerService;
        this.petService = petService;
        this.clock = clock;
        this.checkupConfiguration = checkupConfiguration;
    }

    @ApplicationModuleListener
    public void onPetAdoptedEvent(PetAdoptedEvent event) {
        Owner owner = ownerRepository.findByOwnerId(event.ownerId()).orElseGet(() -> {
            OwnerResponse ownerResponse = ownerService.getOwnerById(event.ownerId())
                    .orElseThrow(() -> NotFoundException.withTypeAndId("OWNER", event.ownerId()));
            return Owner.fromOwnerResponse(ownerResponse);
        });

        owner.adoptPet(
                petService.getPet(event.petId()).map((PetResponse petResponse) -> Pet.fromPetResponse(petResponse, LocalDate.now(clock)))
                        .orElseThrow(() -> NotFoundException.withTypeAndId("PET", event.petId()))
        );

        ownerRepository.save(owner);
    }

    @Transactional
    public void registerCheckUp(Integer petId, LocalDate checkupDate) {
        petRepository.findByPetId(petId).map(
                pet -> {
                    pet.registerVetCheck(checkupDate);
                    return pet;
                }
        ).orElseThrow(() -> NotFoundException.withTypeAndId("PET", petId));
    }

    public VetCheckupPriceResponse getCheckupPrice(Integer ownerId, Integer petId) {
        Owner owner = ownerRepository.findByOwnerId(ownerId).orElseThrow(() -> NotFoundException.withTypeAndId("OWNER", ownerId));
        Pet pet = owner.findPet(petId)
                .orElseThrow(() -> NotFoundException.withTypeAndId("PET", petId));

        //region Comment out during refactoring demo, replace with extracted implementation
        // Discount for owners based on number of pets:
        int petCount = owner.getPets().size();

        BigDecimal basePrice = checkupConfiguration.getPrice();
        BigDecimal discount = BigDecimal.ZERO;
        // Applicable discounts are configured in the application.yml file.
        if (petCount >= 5) {
            discount = basePrice.multiply(checkupConfiguration.getDiscount().get(5));
        } else if (petCount >= 2) {
            discount = basePrice.multiply(checkupConfiguration.getDiscount().get(2));
        }

        BigDecimal discountedPrice = basePrice.subtract(discount);

        // Fine based on last pet checkup:
        BigDecimal fine = BigDecimal.ZERO;
        if (pet.getLastVetCheck().isBefore(LocalDate.now(clock).minusYears(4))) {
            fine = discountedPrice.multiply(checkupConfiguration.getFines().getPercentage().get(4));
        } else if (pet.getLastVetCheck().isBefore(LocalDate.now(clock).minusYears(3))) {
            fine = discountedPrice.multiply(checkupConfiguration.getFines().getPercentage().get(3));
        } else if (pet.getLastVetCheck().isBefore(LocalDate.now(clock).minusYears(2))) {
            fine = checkupConfiguration.getFines().getFixed().get(2);
        } else if (pet.getLastVetCheck().isBefore(LocalDate.now(clock).minusYears(1))) {
            fine = checkupConfiguration.getFines().getFixed().get(1);
        }

        // Total checkup price:
        BigDecimal checkupPrice = discountedPrice.add(fine).setScale(2, RoundingMode.HALF_UP);
        //endregion

//        //region extracted implementation
//        BigDecimal checkupPrice = checkupPriceCalculator.calculateCheckupPrice(owner, pet);
//        //endregion

        return new VetCheckupPriceResponse(ownerId, petId, checkupPrice);
    }
}
