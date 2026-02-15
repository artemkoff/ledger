package com.artemkoff.ledger.domain.money;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CurrencyTests {

    @Test
    void normalizeToUppercase() {
        var c = Currency.of("eur");
        assertThat(c.value()).isEqualTo("EUR");
    }

    @Test
    void trimWhitespace() {
        var c = Currency.of(" usd   ");
        assertThat(c.value()).isEqualTo("USD");
    }

    @Test
    void rejectsNullValue() {
        assertThatThrownBy(() -> new Currency(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsNonThreeLetterValues() {
        assertThatThrownBy(() -> new Currency("EU"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Currency("USDT"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsInvalidSymbol() {
        assertThatThrownBy(() -> new Currency("E1R"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
