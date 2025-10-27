package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class PercentageFineCalculatorTest {
    PercentageFineCalculator fineCalculator;
    VeterinaryCheckUpConfigProperties configProperties;

    @BeforeEach
    void setUp() {
        configProperties = TestVeterinaryCheckUpConfigProperties.testDefaults();
        fineCalculator = new PercentageFineCalculator(configProperties);
    }

    @TestFactory
    Stream<DynamicTest> calculateFine() {
        return Stream.of(
                Pair.of(3, BigDecimal.valueOf(25)),
                Pair.of(4, BigDecimal.valueOf(37.5)),
                Pair.of(5, BigDecimal.valueOf(37.5)),
                Pair.of(15, BigDecimal.valueOf(37.5))
        ).map(args -> dynamicTest(
                "If last checkup was %d years ago, a fine of %.2f is charged".formatted(args.getLeft(), args.getRight()),
                () -> assertThat(
                        fineCalculator.calculateFine(args.getLeft(), configProperties.getPrice())
                ).isEqualTo(args.getRight().setScale(2, HALF_UP))
        ));
    }
}