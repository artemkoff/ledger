package com.artemkoff.ledger.domain.ledger;

import com.github.f4b6a3.uuid.UuidCreator;

import java.util.Objects;
import java.util.UUID;

public record EntryId(UUID value) {
    public EntryId {
        Objects.requireNonNull(value, "entry id cannot be null");
    }

    public static EntryId newId() {
        return new EntryId(UuidCreator.getTimeOrderedEpoch());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
