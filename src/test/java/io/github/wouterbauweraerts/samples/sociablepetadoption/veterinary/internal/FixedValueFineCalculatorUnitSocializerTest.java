package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import io.github.wouterbauweraerts.unitsocializer.core.annotations.Predefined;
import io.github.wouterbauweraerts.unitsocializer.core.annotations.TestSubject;
import io.github.wouterbauweraerts.unitsocializer.junit.mockito.annotations.SociableTest;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@SociableTest
class FixedValueFineCalculatorUnitSocializerTest {
    @TestSubject
    FixedValueFineCalculator fineCalculator;
    @Predefined
    VeterinaryCheckUpConfigProperties configProperties = TestVeterinaryCheckUpConfigProperties.testDefaults();

    @TestFactory
    Stream<DynamicTest> calculateFine() {
        return Stream.of(
                Pair.of(1, configProperties.getFines().getFixed().get(1)),
                Pair.of(2, configProperties.getFines().getFixed().get(2))
        ).map(args -> dynamicTest(
                        "If last checkup was %d years ago, a fine of %.2f is charged".formatted(args.getLeft(), args.getRight()),
                        () -> assertThat(
                                fineCalculator.calculateFine(args.getLeft(), configProperties.getPrice())
                        ).isEqualTo(args.getRight().setScale(2, HALF_UP))
                )
        );
    }
}