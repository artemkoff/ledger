package com.artemkoff.ledger.domain.money;

import java.util.Objects;

public record Money(Currency currency, long minorUnits) {
    public Money {
        Objects.requireNonNull(currency, "Currency cannot be null");
    }

    public static Money of(Currency currency, long minorUnits) {
        return new Money(currency, minorUnits);
    }

    public Money add(Money other) {
        Objects.requireNonNull(other, "Money cannot be null");
        ensureSameCurrency(other);
        return new Money(currency, Math.addExact(minorUnits, other.minorUnits));
    }

    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Money cannot be null");
        ensureSameCurrency(other);
        return new Money(currency, Math.subtractExact(minorUnits, other.minorUnits));
    }

    private void ensureSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch " + this.currency + " vs " + other.currency);
        }
    }
}
