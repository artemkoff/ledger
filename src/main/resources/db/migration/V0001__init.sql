-- V0001__init.sql

CREATE SCHEMA IF NOT EXISTS ledger;

-- ---------- Enums ----------
DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'account_type') THEN
            CREATE TYPE ledger.account_type AS ENUM ('ASSET', 'LIABILITY', 'EQUITY', 'REVENUE', 'EXPENSE');
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'posting_direction') THEN
            CREATE TYPE ledger.posting_direction AS ENUM ('DEBIT', 'CREDIT');
        END IF;
    END
$$;

CREATE TABLE IF NOT EXISTS ledger.accounts
(
    account_id        UUID PRIMARY KEY,
    tenant_id         UUID                NOT NULL,

    external_id       TEXT                NULL,

    name              TEXT                NOT NULL,
    type              ledger.account_type NOT NULL,
    currency          CHAR(3)             NOT NULL, -- ISO 4217
    status            TEXT                NOT NULL DEFAULT 'ACTIVE',

    parent_account_id UUID                NULL REFERENCES ledger.accounts (account_id),

    owner_type        TEXT                NULL,
    owner_id          TEXT                NULL,

    created_at        TIMESTAMPTZ         NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ         NOT NULL DEFAULT now(),
    version           BIGINT              NOT NULL DEFAULT 0,

    metadata          JSONB               NOT NULL DEFAULT '{}'::jsonb
);

CREATE INDEX IF NOT EXISTS accounts_tenant_idx ON ledger.accounts (tenant_id);
CREATE INDEX IF NOT EXISTS accounts_owner_idx ON ledger.accounts (tenant_id, owner_type, owner_id);
CREATE INDEX IF NOT EXISTS accounts_parent_idx ON ledger.accounts (parent_account_id);
CREATE INDEX IF NOT EXISTS accounts_type_currency_idx ON ledger.accounts (tenant_id, type, currency);

CREATE UNIQUE INDEX IF NOT EXISTS accounts_tenant_owner_external_id_uq
    ON ledger.accounts (tenant_id, owner_type, owner_id, external_id)
    WHERE external_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS ledger.journal_entries
(
    entry_id     UUID PRIMARY KEY,
    tenant_id    UUID        NOT NULL,

    external_id  TEXT        NOT NULL,
    description  TEXT        NULL,
    effective_at TIMESTAMPTZ NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    metadata     JSONB       NOT NULL DEFAULT '{}'::jsonb
);

CREATE INDEX IF NOT EXISTS journal_entries_tenant_idx ON ledger.journal_entries (tenant_id);
CREATE INDEX IF NOT EXISTS journal_entries_effective_at_idx ON ledger.journal_entries (tenant_id, effective_at);

CREATE UNIQUE INDEX IF NOT EXISTS journal_entries_tenant_external_id_uq
    ON ledger.journal_entries (tenant_id, external_id);

CREATE TABLE IF NOT EXISTS ledger.postings
(
    posting_id   UUID PRIMARY KEY,
    tenant_id    UUID                     NOT NULL,

    entry_id     UUID                     NOT NULL REFERENCES ledger.journal_entries (entry_id) ON DELETE CASCADE,
    account_id   UUID                     NOT NULL REFERENCES ledger.accounts (account_id),

    direction    ledger.posting_direction NOT NULL,
    amount_minor BIGINT                   NOT NULL CHECK (amount_minor > 0),

    created_at   TIMESTAMPTZ              NOT NULL DEFAULT now(),
    metadata     JSONB                    NOT NULL DEFAULT '{}'::jsonb
);

CREATE INDEX IF NOT EXISTS postings_entry_idx ON ledger.postings (entry_id);
CREATE INDEX IF NOT EXISTS postings_account_created_idx ON ledger.postings (account_id, created_at);

CREATE INDEX IF NOT EXISTS postings_tenant_account_created_idx ON ledger.postings (tenant_id, account_id, created_at);
CREATE INDEX IF NOT EXISTS postings_tenant_entry_idx ON ledger.postings (tenant_id, entry_id);

CREATE TABLE IF NOT EXISTS ledger.account_balances
(
    account_id    UUID PRIMARY KEY REFERENCES ledger.accounts (account_id) ON DELETE CASCADE,
    tenant_id     UUID        NOT NULL,

    balance_minor BIGINT      NOT NULL DEFAULT 0,
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    version       BIGINT      NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS account_balances_tenant_idx ON ledger.account_balances (tenant_id);
CREATE INDEX IF NOT EXISTS account_balances_balance_idx ON ledger.account_balances (tenant_id, balance_minor);
