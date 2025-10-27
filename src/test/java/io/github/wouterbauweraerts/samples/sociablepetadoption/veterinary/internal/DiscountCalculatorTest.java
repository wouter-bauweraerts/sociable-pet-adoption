package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import io.github.wouterbauweraerts.unitsocializer.core.annotations.Predefined;
import io.github.wouterbauweraerts.unitsocializer.core.annotations.TestSubject;
import io.github.wouterbauweraerts.unitsocializer.junit.mockito.annotations.SociableTest;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@SociableTest
class DiscountCalculatorTest {
    @TestSubject
    DiscountCalculator discountCalculator;
    @Predefined
    VeterinaryCheckUpConfigProperties configProperties = TestVeterinaryCheckUpConfigProperties.testDefaults();

    @Test
    void calculateDiscount_singlePet_noDiscount() {
        assertThat(
                discountCalculator.calculateDiscount(
                        VetOwnerFixtures.anOwnerWithPetsNoFines(1),
                        configProperties.getPrice()
                )
        ).isEqualTo(BigDecimal.ZERO.setScale(2, HALF_UP));
    }

    @TestFactory
    Stream<DynamicTest> calculateDiscount_twoOrMorePetsDiscount_expectedDiscount() {
        return IntStream.range(2,5)
                .mapToObj(numberOfPets -> dynamicTest(
                        "Owner with %d pets gets a discount of 5".formatted(numberOfPets),
                        () -> assertThat(discountCalculator.calculateDiscount(
                                VetOwnerFixtures.anOwnerWithPetsNoFines(numberOfPets),
                                configProperties.getPrice()
                        )).isEqualTo(BigDecimal.valueOf(5).setScale(2, HALF_UP))
                ));
    }

    @TestFactory
    Stream<DynamicTest> calculateDiscount_fiveOrMorePetsDiscount_expectedDiscount() {
        return IntStream.range(5, 10)
                .mapToObj(numberOfPets -> dynamicTest(
                        "Owner with %d pets gets a discount of 12.5".formatted(numberOfPets),
                        () -> assertThat(discountCalculator.calculateDiscount(
                                VetOwnerFixtures.anOwnerWithPetsNoFines(numberOfPets),
                                configProperties.getPrice()
                        )).isEqualTo(BigDecimal.valueOf(12.5).setScale(2, HALF_UP))
                ));
    }
}