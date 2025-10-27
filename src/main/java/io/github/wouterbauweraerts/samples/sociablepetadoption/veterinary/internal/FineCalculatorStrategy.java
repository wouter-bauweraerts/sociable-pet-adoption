package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;

import static io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal.FineCalculator.calculateDiffInYears;
import static java.math.RoundingMode.HALF_UP;

@Component
public class FineCalculatorStrategy {
    private final Clock clock;
    private final FixedValueFineCalculator fixedValueFineCalculator;
    private final PercentageFineCalculator percentageFineCalculator;

    public FineCalculatorStrategy(Clock clock, FixedValueFineCalculator fixedValueFineCalculator, PercentageFineCalculator percentageFineCalculator) {
        this.clock = clock;
        this.fixedValueFineCalculator = fixedValueFineCalculator;
        this.percentageFineCalculator = percentageFineCalculator;
    }

    public BigDecimal calculateFine(Pet pet, BigDecimal discountedPrice) {
        int yearsSinceLastCheck = calculateDiffInYears(pet.getLastVetCheck(), LocalDate.now(clock).minusDays(1));
        return switch (yearsSinceLastCheck) {
            case 0 -> BigDecimal.ZERO.setScale(2, HALF_UP);
            case 1, 2 -> fixedValueFineCalculator.calculateFine(yearsSinceLastCheck, discountedPrice);
            default -> percentageFineCalculator.calculateFine(yearsSinceLastCheck, discountedPrice);
        };
    }
}
