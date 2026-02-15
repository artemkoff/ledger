package com.artemkoff.ledger.domain.tenant;

import java.util.Objects;
import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;

public record TenantId(UUID value) {
    public TenantId {
        Objects.requireNonNull(value, "Tenant id cannot be null");
    }

    public static TenantId newId() {
        return new TenantId(UuidCreator.getTimeOrderedEpoch());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
