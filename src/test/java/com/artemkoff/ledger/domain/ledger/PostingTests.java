package com.artemkoff.ledger.domain.ledger;

import com.artemkoff.ledger.domain.account.AccountId;
import com.artemkoff.ledger.domain.common.JsonData;
import com.artemkoff.ledger.domain.money.Currency;
import com.artemkoff.ledger.domain.money.Money;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

public class PostingTests {
    @Test
    void createsPosting() {
        var posting = new Posting(
                new PostingId(java.util.UUID.randomUUID()),
                EntryId.newId(),
                AccountId.newId(),
                Money.of(Currency.of("EUR"), 100),
                PostingDirection.DEBIT,
                Instant.now(),
                JsonData.empty()
        );

        assertThat(posting).isNotNull();
        assertThat(posting.amount().minorUnits()).isEqualTo(100);
        assertThat(posting.direction()).isEqualTo(PostingDirection.DEBIT);
    }

    @Test
    void defaultsMetadataToEmptyWhenNull() {
        var posting = new Posting(
                new PostingId(java.util.UUID.randomUUID()),
                EntryId.newId(),
                AccountId.newId(),
                Money.of(Currency.of("EUR"), 100),
                PostingDirection.CREDIT,
                Instant.now(),
                null
        );

        assertThat(posting.metadata()).isNotNull();
        assertThat(posting.metadata().isEmpty()).isTrue();
    }

    @Test
    void rejectsNonPositiveAmount() {
        assertThatThrownBy(() -> new Posting(
                new PostingId(java.util.UUID.randomUUID()),
                EntryId.newId(),
                AccountId.newId(),
                Money.of(Currency.of("EUR"), 0),
                PostingDirection.DEBIT,
                Instant.now(),
                JsonData.empty()
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNullEntryId() {
        assertThatThrownBy(() -> new Posting(
                new PostingId(java.util.UUID.randomUUID()),
                null,
                AccountId.newId(),
                Money.of(Currency.of("EUR"), 100),
                PostingDirection.DEBIT,
                Instant.now(),
                JsonData.empty()
        )).isInstanceOf(NullPointerException.class);
    }
}
