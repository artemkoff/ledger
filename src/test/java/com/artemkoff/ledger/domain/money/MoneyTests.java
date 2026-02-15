package com.artemkoff.ledger.domain.money;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class MoneyTests {
    @Test
    void createsMoney() {
        var m = Money.of(Currency.of("EUR"), 450);
        assertThat(m).isInstanceOf(Money.class);
        assertThat(m.currency().value()).isEqualTo("EUR");
        assertThat(m.minorUnits()).isEqualTo(450);
    }

    @Test
    void rejectsNullCurrency() {
        assertThatThrownBy(() -> Money.of(null, 100))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addition() {
        var m1 = Money.of(Currency.of("USD"), 100);
        var m2 = Money.of(Currency.of("USD"), 250);

        var sum = m1.add(m2);
        assertThat(sum.minorUnits()).isEqualTo(350);
        assertThat(sum.currency().value()).isEqualTo("USD");
    }

    @Test
    void subtraction() {
        var m1 = Money.of(Currency.of("USD"), 100);
        var m2 = Money.of(Currency.of("USD"), 250);

        var sum = m1.subtract(m2);
        assertThat(sum.minorUnits()).isEqualTo(-150);
        assertThat(sum.currency().value()).isEqualTo("USD");
    }

    @Test
    void detectsOverflow() {
        var m1 = Money.of(Currency.of("USD"), Long.MAX_VALUE);

        assertThatThrownBy(() -> m1.add(Money.of(Currency.of("USD"), 100)))
                .isInstanceOf(ArithmeticException.class);
    }
}
