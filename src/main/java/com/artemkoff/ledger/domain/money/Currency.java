package com.artemkoff.ledger.domain.money;

import java.util.Locale;
import java.util.Objects;

public record Currency(String value) {
    public Currency {
        Objects.requireNonNull(value, "Currencies must not be null");
        var v = value.trim().toUpperCase(Locale.ROOT);
        if (v.length() != 3 || !v.chars().allMatch(Character::isLetter)) {
            throw new IllegalArgumentException("Invalid currency code: " + value);
        }

        value = v;
    }

    public static Currency of(String value) {
        return new Currency(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
