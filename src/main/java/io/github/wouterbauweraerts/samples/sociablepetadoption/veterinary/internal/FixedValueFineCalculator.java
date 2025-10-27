package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

import static java.math.RoundingMode.HALF_UP;

@Component
public class FixedValueFineCalculator implements FineCalculator {
    private final Map<Integer, BigDecimal> fines;

    public FixedValueFineCalculator(VeterinaryCheckUpConfigProperties configProperties) {
        fines = configProperties.getFines().getFixed();
    }

    @Override
    public BigDecimal calculateFine(int yearsSinceLastVisit, BigDecimal price) {
        return fines.get(yearsSinceLastVisit).setScale(2, HALF_UP);
    }
}
