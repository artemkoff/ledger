package com.artemkoff.ledger.domain.account;

import com.artemkoff.ledger.domain.exceptions.DomainException;
import com.artemkoff.ledger.domain.money.Currency;
import com.artemkoff.ledger.domain.tenant.TenantId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

public class AccountTests {
    private final Instant NOW = Instant.now();

    record Case(String caseName, TenantId accountTenantId, String accountName, AccountType accountType,
                Currency accountCurrency, Instant now, Class<? extends Throwable> expectedException) {
    }

    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("cases")
    public void createsAccount(Case c) {
        Supplier<?> createFn = () -> Account.create(c.accountTenantId(), c.accountName(), c.accountType(), c.accountCurrency(), c.now());

        if (c.expectedException() != null) {
            assertThatThrownBy(createFn::get).isInstanceOf(c.expectedException());
        } else {
            var account = (Account) createFn.get();
            assertThat(account.getTenantId()).isEqualTo(c.accountTenantId());
            assertThat(account.getName()).isEqualTo(c.accountName());
            assertThat(account.getCurrency()).isEqualTo(c.accountCurrency());
            assertThat(account.getType()).isEqualTo(c.accountType());
            assertThat(account.getCreatedAt()).isEqualTo(c.now());
            assertThat(account.getUpdatedAt()).isEqualTo(c.now());
            assertThat(account.getVersion()).isEqualTo(0);
            assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        }
    }

    @Test
    public void closesAccount() {
        var account = Account.create(
                TenantId.newId(),
                "Test Account",
                AccountType.ASSET,
                Currency.of("EUR"),
                NOW
        );
        var closeTime = Instant.now();

        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.getUpdatedAt()).isEqualTo(NOW);
        assertThatNoException().isThrownBy(() -> account.close(closeTime));
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
        assertThat(account.getUpdatedAt()).isEqualTo(closeTime);
        assertThatNoException().isThrownBy(() -> account.close(NOW));
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
        assertThat(account.getUpdatedAt()).isEqualTo(closeTime);
    }

    @Test
    public void suspendsAccount() {
        var account = Account.create(
                TenantId.newId(),
                "Test Account",
                AccountType.ASSET,
                Currency.of("EUR"),
                NOW
        );
        var suspendedTime = Instant.now();
        var closeTime = Instant.now();

        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.getUpdatedAt()).isEqualTo(NOW);
        assertThatNoException().isThrownBy(() -> account.suspend(suspendedTime));
        assertThat(account.getStatus()).isEqualTo(AccountStatus.SUSPENDED);
        assertThat(account.getUpdatedAt()).isEqualTo(suspendedTime);

        assertThatNoException().isThrownBy(() -> account.suspend(NOW));
        assertThat(account.getStatus()).isEqualTo(AccountStatus.SUSPENDED);
        assertThat(account.getUpdatedAt()).isEqualTo(suspendedTime);

        assertThatNoException().isThrownBy(() -> account.close(closeTime));
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
        assertThat(account.getUpdatedAt()).isEqualTo(closeTime);

        assertThatThrownBy(() -> account.suspend(suspendedTime)).isInstanceOf(DomainException.class);
    }

    static Stream<Case> cases() {
        var tenantId = TenantId.newId();
        var name = "Test Account";
        var accountType = AccountType.ASSET;
        var currency = Currency.of("EUR");
        var now = Instant.now();
        return Stream.of(
                new Case("created ok", tenantId, name, accountType, currency, now, null),
                new Case("failed[tenantId is null]", null, name, accountType, currency, now, NullPointerException.class),
                new Case("failed[name is null]", tenantId, null, accountType, currency, now, DomainException.class),
                new Case("failed[currency is null]", tenantId, name, accountType, null, now, NullPointerException.class)
        );
    }
}
