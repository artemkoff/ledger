package com.artemkoff.ledger.domain.ledger;

import com.artemkoff.ledger.domain.account.AccountId;
import com.artemkoff.ledger.domain.common.JsonData;
import com.artemkoff.ledger.domain.money.Currency;
import com.artemkoff.ledger.domain.money.Money;
import com.artemkoff.ledger.domain.tenant.TenantId;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public final class JournalEntry {
    public final EntryId entryId;
    public final TenantId tenantId;
    public final String externalId;
    public final String description;
    public final Instant effectiveAt;
    public final Instant createdAt;
    public final JsonData metadata;

    public final List<Posting> postings;

    private JournalEntry(
            EntryId entryId,
            TenantId tenantId,
            String externalId,
            String description,
            Instant effectiveAt,
            Instant createdAt,
            JsonData metadata,
            List<Posting> postings
    ) {
        this.entryId = Objects.requireNonNull(entryId, "journalEntry.entryId must not be null");
        this.tenantId = Objects.requireNonNull(tenantId, "journalEntry.tenantId must not be null");
        this.externalId = normalizeExternalId(externalId);
        this.description = description;
        this.effectiveAt = Objects.requireNonNull(effectiveAt, "journalEntry.effectiveAt must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "journalEntry.createdAt must not be null");
        this.metadata = metadata == null ? JsonData.empty() : metadata;
        this.postings = List.copyOf(Objects.requireNonNull(postings, "journalEntry.postings must not be null"));

        LedgerRules.validateJournalEntry(this);
    }

    public static JournalEntry create(
            EntryId entryId,
            TenantId tenantId,
            String externalId,
            String description,
            Instant effectiveAt,
            Instant createdAt,
            JsonData metadata,
            List<Posting> postings
    ) {
        return new JournalEntry(entryId, tenantId, externalId, description, effectiveAt, createdAt, metadata, postings);
    }

    public Currency getCurrency() {
        return postings.getFirst().amount().currency();
    }

    public Money getTotalDebits() {
        return sum(PostingDirection.DEBIT);
    }

    public Money getTotalCredits() {
        return sum(PostingDirection.CREDIT);
    }

    private Money sum(PostingDirection direction) {
        var currency = getCurrency();
        var total = postings.stream()
                .filter(p -> p.direction() == direction)
                .mapToLong(p -> p.amount().minorUnits())
                .sum();
        return Money.of(currency, total);
    }

    public EntryId getEntryId() { return entryId; }

    public TenantId getTenantId() { return tenantId; }

    public String getExternalId() { return externalId; }

    public String getDescription() { return description; }

    public Instant getEffectiveAt() { return effectiveAt; }

    public Instant getCreatedAt() { return createdAt; }

    public JsonData getMetadata() { return metadata; }

    public List<Posting> getPostings() { return postings; }

    private static String normalizeExternalId(String externalId) {
        if (externalId == null || externalId.isBlank()) {
            throw new IllegalArgumentException("externalId must not be null or blank");
        }

        return externalId.trim();
    }
}
