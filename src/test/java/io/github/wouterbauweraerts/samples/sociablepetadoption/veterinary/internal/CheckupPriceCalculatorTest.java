package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.time.*;
import java.util.stream.Stream;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class CheckupPriceCalculatorTest {
    CheckupPriceCalculator priceCalculator;
    VeterinaryCheckUpConfigProperties configProperties;
    Clock clock;

    @BeforeEach
    void setUp() {
        Instant instant = LocalDateTime.of(2025, 10, 12, 11, 12, 13, 456).toInstant(ZoneOffset.UTC);
        clock = Clock.fixed(instant, ZoneId.systemDefault());

        configProperties = TestVeterinaryCheckUpConfigProperties.testDefaults();

        priceCalculator = new CheckupPriceCalculator(
                configProperties,
                new DiscountCalculator(configProperties),
                new FineCalculatorStrategy(
                        clock,
                        new FixedValueFineCalculator(configProperties),
                        new PercentageFineCalculator(configProperties)
                )
        );
    }

    @Nested
    class DiscountFlow {
        @TestFactory
        Stream<DynamicTest> getCheckupPrice_assertDiscountFlow() {
            return Stream.of(
                    Pair.of(1, BigDecimal.valueOf(50).setScale(2, HALF_UP)),
                    Pair.of(2, BigDecimal.valueOf(45).setScale(2, HALF_UP)),
                    Pair.of(3, BigDecimal.valueOf(45).setScale(2, HALF_UP)),
                    Pair.of(4, BigDecimal.valueOf(45).setScale(2, HALF_UP)),
                    Pair.of(5, BigDecimal.valueOf(37.5).setScale(2, HALF_UP)),
                    Pair.of(6, BigDecimal.valueOf(37.5).setScale(2, HALF_UP))
            ).map(args -> dynamicTest(
                    "an owner with %d pets pays %.2f without fines".formatted(args.getLeft(), args.getRight().doubleValue()),
                    () -> {
                        Owner owner = VetOwnerFixtures.anOwnerWithPetsNoFines(args.getLeft());
                        Pet pet = owner.getPets().getFirst();

                        assertThat(priceCalculator.calculateCheckupPrice(owner, pet))
                                .isEqualTo(args.getRight());
                    }
            ));
        }
    }

    @Nested
    class FineFlow {
        @TestFactory
        Stream<DynamicTest> getCheckupDate_withFine_returnsExpected() {
            return Stream.of(
                    Pair.of(LocalDate.now(clock).minusYears(1), BigDecimal.valueOf(50)),
                    Pair.of(LocalDate.now(clock).minusYears(1).minusDays(1), BigDecimal.valueOf(60)),
                    Pair.of(LocalDate.now(clock).minusYears(2).plusDays(1), BigDecimal.valueOf(60)),
                    Pair.of(LocalDate.now(clock).minusYears(2), BigDecimal.valueOf(60)),
                    Pair.of(LocalDate.now(clock).minusYears(2).minusDays(1), BigDecimal.valueOf(65)),
                    Pair.of(LocalDate.now(clock).minusYears(3).plusDays(1), BigDecimal.valueOf(65)),
                    Pair.of(LocalDate.now(clock).minusYears(3), BigDecimal.valueOf(65)),
                    Pair.of(LocalDate.now(clock).minusYears(3).minusDays(1), BigDecimal.valueOf(75)),
                    Pair.of(LocalDate.now(clock).minusYears(4), BigDecimal.valueOf(75)),
                    Pair.of(LocalDate.now(clock).minusYears(4).minusDays(1), BigDecimal.valueOf(87.5)),
                    Pair.of(LocalDate.now(clock).minusYears(5), BigDecimal.valueOf(87.5)),
                    Pair.of(LocalDate.now(clock).minusYears(10), BigDecimal.valueOf(87.5))
            ).map(args -> dynamicTest(
                    "checkup price since last checkup %s is equal to %.2f".formatted(
                            Period.between(args.getLeft(), LocalDate.now(clock)).toString(),
                            args.getRight()
                    ),
                    () -> {
                        Owner owner = VetOwnerFixtures.anOwnerWithPetsNoFines(1, args.getLeft());
                        Pet pet = owner.getPets().getFirst();


                        assertThat(priceCalculator.calculateCheckupPrice(owner, pet))
                                .isEqualTo(args.getRight().setScale(2, HALF_UP));
                    }
            ));
        }
    }

    @Nested
    class DiscountsAndFineFlows {
        @TestFactory
        Stream<DynamicTest> getCheckupDate_twoOrMorePetsDiscount_moreThanOneYearAgoFine_returnsExpected() {
            return Stream.of(
                    Triple.of(2, LocalDate.now(clock).minusMonths(13), BigDecimal.valueOf(55)),
                    Triple.of(2, LocalDate.now(clock).minusYears(2), BigDecimal.valueOf(55)),
                    Triple.of(3, LocalDate.now(clock).minusMonths(13), BigDecimal.valueOf(55)),
                    Triple.of(3, LocalDate.now(clock).minusYears(2), BigDecimal.valueOf(55)),
                    Triple.of(4, LocalDate.now(clock).minusMonths(13), BigDecimal.valueOf(55)),
                    Triple.of(4, LocalDate.now(clock).minusYears(2), BigDecimal.valueOf(55)),
                    Triple.of(5, LocalDate.now(clock).minusMonths(13), BigDecimal.valueOf(47.5)),
                    Triple.of(5, LocalDate.now(clock).minusYears(2), BigDecimal.valueOf(47.5))
            ).map(args -> dynamicTest(
                    "Owner owns %d pets and gets a fine because the last vet checkup was between 1 and 2 years ago. Checkup costs %.2f".formatted(
                            args.getLeft(),
                            args.getRight()
                    ),
                    () -> {
                        Owner owner = VetOwnerFixtures.anOwnerWithPetsNoFines(args.getLeft(), args.getMiddle());
                        Pet pet = owner.getPets().getFirst();

                        assertThat(priceCalculator.calculateCheckupPrice(owner, pet))
                                .isEqualTo(args.getRight().setScale(2, HALF_UP));
                    }
            ));
        }

        @TestFactory
        Stream<DynamicTest> getCheckupDate_twoOrMorePetsDiscount_moreThanTwoYearAgoFine_returnsExpected() {
            return Stream.of(
                    Triple.of(2, LocalDate.now(clock).minusMonths(25), BigDecimal.valueOf(60)),
                    Triple.of(2, LocalDate.now(clock).minusYears(3), BigDecimal.valueOf(60)),
                    Triple.of(3, LocalDate.now(clock).minusMonths(25), BigDecimal.valueOf(60)),
                    Triple.of(3, LocalDate.now(clock).minusYears(3), BigDecimal.valueOf(60)),
                    Triple.of(4, LocalDate.now(clock).minusMonths(25), BigDecimal.valueOf(60)),
                    Triple.of(4, LocalDate.now(clock).minusYears(3), BigDecimal.valueOf(60)),
                    Triple.of(5, LocalDate.now(clock).minusMonths(25), BigDecimal.valueOf(52.5)),
                    Triple.of(5, LocalDate.now(clock).minusYears(3), BigDecimal.valueOf(52.5))
            ).map(args -> dynamicTest(
                    "Owner owns %d pets and gets a fine because the last vet checkup was between 2 and 3 years ago. Checkup costs %.2f".formatted(
                            args.getLeft(),
                            args.getRight()
                    ),
                    () -> {
                        Owner owner = VetOwnerFixtures.anOwnerWithPetsNoFines(args.getLeft(), args.getMiddle());
                        Pet pet = owner.getPets().getFirst();

                        assertThat(priceCalculator.calculateCheckupPrice(owner, pet))
                                .isEqualTo(args.getRight().setScale(2, HALF_UP));
                    }
            ));
        }

        @TestFactory
        Stream<DynamicTest> getCheckupDate_twoOrMorePetsDiscount_moreThanThreeYearAgoFine_returnsExpected() {
            return Stream.of(
                    Triple.of(2, LocalDate.now(clock).minusMonths(37), BigDecimal.valueOf(67.5)),
                    Triple.of(2, LocalDate.now(clock).minusYears(4), BigDecimal.valueOf(67.5)),
                    Triple.of(3, LocalDate.now(clock).minusMonths(37), BigDecimal.valueOf(67.5)),
                    Triple.of(3, LocalDate.now(clock).minusYears(4), BigDecimal.valueOf(67.5)),
                    Triple.of(4, LocalDate.now(clock).minusMonths(37), BigDecimal.valueOf(67.5)),
                    Triple.of(4, LocalDate.now(clock).minusYears(4), BigDecimal.valueOf(67.5)),
                    Triple.of(5, LocalDate.now(clock).minusMonths(37), BigDecimal.valueOf(56.25)),
                    Triple.of(5, LocalDate.now(clock).minusYears(4), BigDecimal.valueOf(56.25)),
                    Triple.of(8, LocalDate.now(clock).minusYears(4), BigDecimal.valueOf(56.25))
            ).map(args -> dynamicTest(
                    "Owner owns %d pets and gets a fine because the last vet checkup was between 3 and 4 years ago. Checkup costs %.2f".formatted(
                            args.getLeft(),
                            args.getRight()
                    ),
                    () -> {
                        Owner owner = VetOwnerFixtures.anOwnerWithPetsNoFines(args.getLeft(), args.getMiddle());
                        Pet pet = owner.getPets().getFirst();

                        assertThat(priceCalculator.calculateCheckupPrice(owner, pet))
                                .isEqualTo(args.getRight().setScale(2, HALF_UP));
                    }
            ));
        }

        @TestFactory
        Stream<DynamicTest> getCheckupDate_twoOrMorePetsDiscount_moreThanFourYearAgoFine_returnsExpected() {
            return Stream.of(
                    Triple.of(2, LocalDate.now(clock).minusMonths(49), BigDecimal.valueOf(78.75)),
                    Triple.of(2, LocalDate.now(clock).minusYears(5), BigDecimal.valueOf(78.75)),
                    Triple.of(2, LocalDate.now(clock).minusYears(6), BigDecimal.valueOf(78.75)),
                    Triple.of(3, LocalDate.now(clock).minusMonths(49), BigDecimal.valueOf(78.75)),
                    Triple.of(3, LocalDate.now(clock).minusYears(5), BigDecimal.valueOf(78.75)),
                    Triple.of(3, LocalDate.now(clock).minusYears(6), BigDecimal.valueOf(78.75)),
                    Triple.of(4, LocalDate.now(clock).minusMonths(49), BigDecimal.valueOf(78.75)),
                    Triple.of(4, LocalDate.now(clock).minusYears(5), BigDecimal.valueOf(78.75)),
                    Triple.of(4, LocalDate.now(clock).minusYears(6), BigDecimal.valueOf(78.75)),
                    Triple.of(5, LocalDate.now(clock).minusMonths(49), BigDecimal.valueOf(65.63)),
                    Triple.of(5, LocalDate.now(clock).minusYears(5), BigDecimal.valueOf(65.63)),
                    Triple.of(5, LocalDate.now(clock).minusYears(6), BigDecimal.valueOf(65.63)),
                    Triple.of(9, LocalDate.now(clock).minusYears(6), BigDecimal.valueOf(65.63))
            ).map(args -> dynamicTest(
                    "Owner owns %d pets and gets a fine because the last vet checkup was more than 4 years ago. Checkup costs %.2f".formatted(
                            args.getLeft(),
                            args.getRight()
                    ),
                    () -> {
                        Owner owner = VetOwnerFixtures.anOwnerWithPetsNoFines(args.getLeft(), args.getMiddle());
                        Pet pet = owner.getPets().getFirst();

                        assertThat(priceCalculator.calculateCheckupPrice(owner, pet))
                                .isEqualTo(args.getRight().setScale(2, HALF_UP));
                    }
            ));
        }
    }
}