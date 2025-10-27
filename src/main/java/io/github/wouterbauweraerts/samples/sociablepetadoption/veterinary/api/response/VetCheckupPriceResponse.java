package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.api.response;

import java.math.BigDecimal;

public record VetCheckupPriceResponse(
        Integer ownerId,
        Integer petId,
        BigDecimal price
) {
}
