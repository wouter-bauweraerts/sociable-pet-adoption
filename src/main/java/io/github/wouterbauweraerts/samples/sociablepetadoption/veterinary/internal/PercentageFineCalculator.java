package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

import static java.math.RoundingMode.HALF_UP;

/**
 * This class is only used if the last checkup was more than 3 years ago.
 */
@Component
public class PercentageFineCalculator implements FineCalculator {
    private final Map<Integer, BigDecimal> fines;

    public PercentageFineCalculator(VeterinaryCheckUpConfigProperties configProperties) {
        fines = configProperties.getFines().getPercentage();
    }

    @Override
    public BigDecimal calculateFine(int yearsSinceLastVisit, BigDecimal price) {
        return price.multiply(fines.getOrDefault(yearsSinceLastVisit, BigDecimal.valueOf(0.75)))
                .setScale(2, HALF_UP);
    }
}
