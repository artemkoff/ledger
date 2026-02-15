package com.artemkoff.ledger.domain.ledger;

import com.artemkoff.ledger.domain.account.AccountId;
import com.artemkoff.ledger.domain.common.JsonData;
import com.artemkoff.ledger.domain.money.Money;

import java.time.Instant;
import java.util.Objects;

public record Posting(
        PostingId postingId,
        EntryId entryId,
        AccountId accountId,
        Money amount,
        PostingDirection direction,
        Instant createdAt,
        JsonData metadata
) {
    public Posting {
        Objects.requireNonNull(postingId, "posting.postingId must not be null");
        Objects.requireNonNull(entryId, "posting.entryId must not be null");
        Objects.requireNonNull(accountId, "posting.accountId must not be null");
        Objects.requireNonNull(amount, "posting.amount must not be null");
        Objects.requireNonNull(direction, "posting.direction must not be null");
        Objects.requireNonNull(createdAt, "posting.createdAt must not be null");

        metadata = metadata == null ? JsonData.empty() : metadata;

        if (amount.minorUnits() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }
}
