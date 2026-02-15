package com.artemkoff.ledger.domain.ledger;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class EntryIdTests {
    @Test
    void testConstructor() {
        UUID uuid = UUID.randomUUID();
        EntryId entryId = new EntryId(uuid);

        assertThat(entryId).isNotNull();
        assertThat(entryId.value()).isSameAs(uuid);
    }

    @Test
    void testConstructorWithNullUuid() {
        assertThatThrownBy(() -> new EntryId(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void newIdCreatesValue() {
        var id = EntryId.newId();
        assertThat(id).isNotNull();
        assertThat(id.value()).isNotNull();
    }
}
