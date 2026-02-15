package com.artemkoff.ledger.domain.account;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

public class AccountIdTests {
    @Test
    void testConstructor() {
        UUID uuid = UUID.randomUUID();
        AccountId accountId = new AccountId(uuid);

        assertThat(accountId).isNotNull();
        assertThat(accountId.value()).isSameAs(uuid);
    }

    @Test
    public void testConstructorWithNullUuid() {
        assertThatThrownBy(() -> new AccountId(null))
                .isInstanceOf(NullPointerException.class);
    }
}
