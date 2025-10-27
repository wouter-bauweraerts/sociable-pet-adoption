package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public interface FineCalculator {
    BigDecimal calculateFine(int yearsSinceLastVisit, BigDecimal price);

    static int calculateDiffInYears(LocalDate lastCheckup, LocalDate todayInclusive) {
        return Period.between(lastCheckup, todayInclusive).getYears();
    }
}
