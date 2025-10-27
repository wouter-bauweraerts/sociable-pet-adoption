package io.github.wouterbauweraerts.samples.sociablepetadoption.veterinary.internal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "veterinary.checkup")
public class VeterinaryCheckUpConfigProperties {
    private BigDecimal price;
    private Map<Integer, BigDecimal> discount;
    private Fines fines;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public Map<Integer, BigDecimal> getDiscount() {
        return discount;
    }

    public void setDiscount(Map<Integer, BigDecimal> discount) {
        this.discount = discount;
    }

    public Fines getFines() {
        return fines;
    }

    public void setFines(Fines fines) {
        this.fines = fines;
    }

    public static class Fines {
        private Map<Integer, BigDecimal> fixed = new HashMap<>();
        private Map<Integer, BigDecimal> percentage = new HashMap<>();

        public Map<Integer, BigDecimal> getFixed() {
            return fixed;
        }

        public void setFixed(Map<Integer, BigDecimal> fixed) {
            this.fixed = fixed;
        }

        public Map<Integer, BigDecimal> getPercentage() {
            return percentage;
        }

        public void setPercentage(Map<Integer, BigDecimal> percentage) {
            this.percentage = percentage;
        }
    }
}
