package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import java.math.BigDecimal;
import java.util.Map;

public class TestVeterinaryCheckUpConfigProperties {
    public static VeterinaryCheckUpConfigProperties testDefaults() {
        VeterinaryCheckUpConfigProperties checkUpConfig = new VeterinaryCheckUpConfigProperties();

        checkUpConfig.setPrice(BigDecimal.valueOf(50.00));
        checkUpConfig.setDiscount(
                Map.of(
                        2, BigDecimal.valueOf(0.10),
                        5, BigDecimal.valueOf(0.25)
                )
        );

        VeterinaryCheckUpConfigProperties.Fines fines = new VeterinaryCheckUpConfigProperties.Fines();
        fines.setFixed(
                Map.of(
                        1, BigDecimal.valueOf(10.00),
                        2, BigDecimal.valueOf(15.00)
                )
        );
        fines.setPercentage(
                Map.of(
                        3, BigDecimal.valueOf(0.5),
                        4, BigDecimal.valueOf(0.75)
                )
        );
        checkUpConfig.setFines(fines);

        return checkUpConfig;
    }
}