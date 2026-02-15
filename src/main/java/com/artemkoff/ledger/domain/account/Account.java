package com.artemkoff.ledger.domain.account;

import com.artemkoff.ledger.domain.exceptions.DomainException;
import com.artemkoff.ledger.domain.money.Currency;
import com.artemkoff.ledger.domain.tenant.TenantId;

import java.time.Instant;
import java.util.Objects;

public class Account {
    private final AccountId id;
    private final TenantId tenantId;
    private final String name;
    private final AccountType type;
    private final Currency currency;

    private AccountStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private long version;

    private Account(
            AccountId id,
            TenantId tenantId,
            String name,
            AccountType type,
            Currency currency,
            AccountStatus status,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        this.id = Objects.requireNonNull(id);
        this.tenantId = Objects.requireNonNull(tenantId);
        this.name = validateName(name);
        this.type = Objects.requireNonNull(type);
        this.currency = Objects.requireNonNull(currency);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
        this.version = version;
    }

    public static Account create(
            TenantId tenantId,
            String name,
            AccountType type,
            Currency currency,
            Instant now
    ) {
        return new Account(
                AccountId.newId(),
                tenantId,
                name,
                type,
                currency,
                AccountStatus.ACTIVE,
                now,
                now,
                0
        );
    }

    public void suspend(Instant now) {
        ensureNotClosed();
        if (status == AccountStatus.SUSPENDED) {
            return;
        }

        status = AccountStatus.SUSPENDED;
        touch(now);
    }

    public void activate(Instant now) {
        ensureNotClosed();
        if (status == AccountStatus.ACTIVE) {
            return;
        }

        status = AccountStatus.ACTIVE;
        touch(now);
    }

    public void close(Instant now) {
        if (status == AccountStatus.CLOSED) {
            return;
        }

        status = AccountStatus.CLOSED;
        touch(now);
    }

    public boolean canPost() {
        return status == AccountStatus.ACTIVE;
    }

    public void assertCanPost() {
        if (canPost()) {
            throw new DomainException("Cannot post to account that has status " + status);
        }
    }

    private void ensureNotClosed() {
        if (status == AccountStatus.CLOSED) {
            throw new DomainException("Account is already closed");
        }
    }

    private void touch(Instant now) {
        updatedAt = now;
        version++;
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Account name cannot be empty");
        }
        return name;
    }

    public AccountId getId() {
        return id;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public String getName() {
        return name;
    }

    public AccountType getType() {
        return type;
    }

    public Currency getCurrency() {
        return currency;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public long getVersion() {
        return version;
    }
}
