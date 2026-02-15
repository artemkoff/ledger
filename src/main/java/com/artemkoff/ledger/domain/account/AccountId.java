package com.artemkoff.ledger.domain.account;

import java.util.Objects;
import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;

public record AccountId(UUID value) {
    public AccountId {
        Objects.requireNonNull(value, "Account id cannot be null");
    }

    public static AccountId newId() {
        return new AccountId(UuidCreator.getTimeOrderedEpoch());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
