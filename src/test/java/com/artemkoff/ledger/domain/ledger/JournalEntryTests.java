package com.artemkoff.ledger.domain.ledger;

import com.artemkoff.ledger.domain.account.AccountId;
import com.artemkoff.ledger.domain.common.JsonData;
import com.artemkoff.ledger.domain.exceptions.ValidationException;
import com.artemkoff.ledger.domain.money.Currency;
import com.artemkoff.ledger.domain.money.Money;
import com.artemkoff.ledger.domain.tenant.TenantId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class JournalEntryTests {
    @Test
    void createsJournalEntry() {
        var entryId = EntryId.newId();
        var now = Instant.now();
        var entry = JournalEntry.create(
                entryId,
                TenantId.newId(),
                "ext-123",
                "test entry",
                now,
                now,
                JsonData.empty(),
                balancedPostings(entryId, 100)
        );

        assertThat(entry.getEntryId()).isEqualTo(entryId);
        assertThat(entry.getExternalId()).isEqualTo("ext-123");
        assertThat(entry.getPostings()).hasSize(2);
    }

    @Test
    void trimsExternalId() {
        var entryId = EntryId.newId();
        var now = Instant.now();

        var entry = JournalEntry.create(
                entryId,
                TenantId.newId(),
                "  ext-123  ",
                null,
                now,
                now,
                null,
                balancedPostings(entryId, 100)
        );

        assertThat(entry.getExternalId()).isEqualTo("ext-123");
    }

    @Test
    void rejectsBlankExternalId() {
        var entryId = EntryId.newId();
        var now = Instant.now();

        assertThatThrownBy(() -> JournalEntry.create(
                entryId,
                TenantId.newId(),
                "   ",
                null,
                now,
                now,
                JsonData.empty(),
                balancedPostings(entryId, 100)
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsSinglePosting() {
        var entryId = EntryId.newId();
        var posting = new Posting(
                new PostingId(java.util.UUID.randomUUID()),
                entryId,
                AccountId.newId(),
                Money.of(Currency.of("EUR"), 100),
                PostingDirection.DEBIT,
                Instant.now(),
                JsonData.empty()
        );

        assertThatThrownBy(() -> JournalEntry.create(
                entryId,
                TenantId.newId(),
                "ext-1",
                null,
                Instant.now(),
                Instant.now(),
                JsonData.empty(),
                List.of(posting)
        )).isInstanceOf(ValidationException.class);
    }

    @Test
    void rejectsUnbalancedPostings() {
        var entryId = EntryId.newId();
        var debit = new Posting(
                new PostingId(java.util.UUID.randomUUID()),
                entryId,
                AccountId.newId(),
                Money.of(Currency.of("EUR"), 100),
                PostingDirection.DEBIT,
                Instant.now(),
                JsonData.empty()
        );
        var credit = new Posting(
                new PostingId(java.util.UUID.randomUUID()),
                entryId,
                AccountId.newId(),
                Money.of(Currency.of("EUR"), 90),
                PostingDirection.CREDIT,
                Instant.now(),
                JsonData.empty()
        );

        assertThatThrownBy(() -> JournalEntry.create(
                entryId,
                TenantId.newId(),
                "ext-1",
                null,
                Instant.now(),
                Instant.now(),
                JsonData.empty(),
                List.of(debit, credit)
        )).isInstanceOf(ValidationException.class);
    }

    private static List<Posting> balancedPostings(EntryId entryId, long amountMinorUnits) {
        return List.of(
                new Posting(
                        new PostingId(java.util.UUID.randomUUID()),
                        entryId,
                        AccountId.newId(),
                        Money.of(Currency.of("EUR"), amountMinorUnits),
                        PostingDirection.DEBIT,
                        Instant.now(),
                        JsonData.empty()
                ),
                new Posting(
                        new PostingId(java.util.UUID.randomUUID()),
                        entryId,
                        AccountId.newId(),
                        Money.of(Currency.of("EUR"), amountMinorUnits),
                        PostingDirection.CREDIT,
                        Instant.now(),
                        JsonData.empty()
                )
        );
    }
}
