package com.artemkoff.ledger.domain.ledger;

import com.github.f4b6a3.uuid.UuidCreator;

import java.util.Objects;
import java.util.UUID;

public record PostingId(UUID value) {
    public PostingId {
        Objects.requireNonNull(value, "Posting id cannot be null");
    }

    public PostingId newId() {
        return new PostingId(UuidCreator.getTimeOrderedEpoch());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
