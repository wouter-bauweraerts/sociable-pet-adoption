package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.time.*;
import java.util.stream.Stream;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class FineCalculatorStrategyTest {
    FineCalculatorStrategy strategy;
    Clock clock;
    VeterinaryCheckUpConfigProperties configProperties;

    @BeforeEach
    void setUp() {
        Instant instant = LocalDateTime.of(2025, 10, 12, 11, 12, 13, 456).toInstant(ZoneOffset.UTC);
        clock = Clock.fixed(instant, ZoneId.systemDefault());
        configProperties = TestVeterinaryCheckUpConfigProperties.testDefaults();

        strategy = new FineCalculatorStrategy(clock, new FixedValueFineCalculator(configProperties), new PercentageFineCalculator(configProperties));
    }

    @TestFactory
    Stream<DynamicTest> calculateFine() {
        return Stream.of(
                Pair.of("P1D", BigDecimal.ZERO),
                Pair.of("P1Y", BigDecimal.ZERO),
                Pair.of("P1Y1D", BigDecimal.valueOf(10)),
                Pair.of("P2Y", BigDecimal.valueOf(10)),
                Pair.of("P2Y1D", BigDecimal.valueOf(15)),
                Pair.of("P3Y", BigDecimal.valueOf(15)),
                Pair.of("P3Y1D", BigDecimal.valueOf(25)),
                Pair.of("P4Y", BigDecimal.valueOf(25)),
                Pair.of("P4Y1D", BigDecimal.valueOf(37.5)),
                Pair.of("P14Y", BigDecimal.valueOf(37.5))
        ).map(args -> dynamicTest(
                "%s since last visit results in a fine of %.2f".formatted(args.getLeft(), args.getRight()),
                () -> {
                    LocalDate lastCheckup = LocalDate.now(clock).minus(Period.parse(args.getLeft()));
                    Pet pet = VetPetFixtures.aPetWithLastCheckupDate(lastCheckup);

                    assertThat(strategy.calculateFine(pet, configProperties.getPrice())).isEqualTo(
                            args.getRight().setScale(2, HALF_UP)
                    );
                }
        ));
    }
}