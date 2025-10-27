package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CheckupPriceCalculator {
    private final VeterinaryCheckUpConfigProperties veterinaryCheckUpConfigProperties;
    private final DiscountCalculator discountCalculator;
    private final FineCalculatorStrategy fineCalculator;

    public CheckupPriceCalculator(VeterinaryCheckUpConfigProperties veterinaryCheckUpConfigProperties, DiscountCalculator discountCalculator, FineCalculatorStrategy fineCalculator) {
        this.veterinaryCheckUpConfigProperties = veterinaryCheckUpConfigProperties;
        this.discountCalculator = discountCalculator;
        this.fineCalculator = fineCalculator;
    }

    public BigDecimal calculateCheckupPrice(Owner owner, Pet pet) {
        BigDecimal discountedPrice = veterinaryCheckUpConfigProperties.getPrice()
                .subtract(discountCalculator.calculateDiscount(owner, veterinaryCheckUpConfigProperties.getPrice()));
        return discountedPrice.add(fineCalculator.calculateFine(pet, discountedPrice)).setScale(2, RoundingMode.HALF_UP);
    }
}
