package com.artemkoff.ledger.domain.ledger;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class PostingIdTests {
    @Test
    void testConstructor() {
        UUID uuid = UUID.randomUUID();
        PostingId postingId = new PostingId(uuid);

        assertThat(postingId).isNotNull();
        assertThat(postingId.value()).isSameAs(uuid);
    }

    @Test
    void testConstructorWithNullUuid() {
        assertThatThrownBy(() -> new PostingId(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void newIdCreatesValue() {
        var id = new PostingId(UUID.randomUUID()).newId();
        assertThat(id).isNotNull();
        assertThat(id.value()).isNotNull();
    }
}
