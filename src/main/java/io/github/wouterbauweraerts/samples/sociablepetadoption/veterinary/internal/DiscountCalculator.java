package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.RoundingMode.HALF_UP;

@Component
public class DiscountCalculator {
    private final VeterinaryCheckUpConfigProperties veterinaryCheckUpConfigProperties;

    public DiscountCalculator(VeterinaryCheckUpConfigProperties veterinaryCheckUpConfigProperties) {
        this.veterinaryCheckUpConfigProperties = veterinaryCheckUpConfigProperties;
    }

    private enum DiscountType {
        NO_DISCOUNT,
        TWO_OR_MORE_PETS,
        FIVE_OR_MORE_PETS;

        public static DiscountType get(int numberOfPets) {
            if (numberOfPets < 2) {
                return NO_DISCOUNT;
            } else if (numberOfPets < 5) {
                return TWO_OR_MORE_PETS;
            } else {
                return FIVE_OR_MORE_PETS;
            }
        }
    }

    public BigDecimal calculateDiscount(Owner owner, BigDecimal basePrice){
        return (switch (DiscountType.get(owner.getNumberOfPets())) {
            case NO_DISCOUNT -> BigDecimal.ZERO;
            case TWO_OR_MORE_PETS -> basePrice.multiply(
                    veterinaryCheckUpConfigProperties.getDiscount().get(2)
            );
            case FIVE_OR_MORE_PETS -> basePrice.multiply(
                    veterinaryCheckUpConfigProperties.getDiscount().get(5)
            );
        }).setScale(2, HALF_UP);
    }
}
