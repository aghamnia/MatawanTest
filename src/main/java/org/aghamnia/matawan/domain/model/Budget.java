package org.aghamnia.matawan.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value object representing a financial budget in euros.
 */
public record Budget(BigDecimal amount) {

    public Budget {
        Objects.requireNonNull(amount, "amount must not be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Budget amount cannot be negative");
        }
    }

    /**
     * Convenience factory method for String input.
     */
    public static Budget of(String amount) {
        return new Budget(new BigDecimal(amount));
    }
}
