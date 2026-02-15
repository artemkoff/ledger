package com.artemkoff.ledger.domain.tenant;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

public class TenantIdTests {
    @Test
    public void testConstructor() {
        var uuid = UUID.randomUUID();
        var id = new TenantId(uuid);

        assertThat(id).isNotNull();
        assertThat(id.toString()).isEqualTo(uuid.toString());
    }

    @Test
    public void testConstructorWithNullUuid() {
        assertThatThrownBy(() -> new TenantId(null))
                .isInstanceOf(NullPointerException.class);
    }
}
