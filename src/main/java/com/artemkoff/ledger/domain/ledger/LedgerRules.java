package com.artemkoff.ledger.domain.ledger;

import com.artemkoff.ledger.domain.exceptions.ValidationException;
import com.artemkoff.ledger.domain.money.Currency;

import java.util.HashSet;

public class LedgerRules {
    private LedgerRules() {}

    public static void validateJournalEntry(JournalEntry entry) {
        if (entry.postings.size() < 2) {
            throw new ValidationException("Journal entry must have at least 2 postings");
        }

        var entryId = entry.getEntryId();
        for (var posting : entry.postings) {
            if (!posting.entryId().equals(entryId)) {
                throw new ValidationException("Journal entry IDs don't match, expected " + entryId + ", found " + posting.entryId());
            }
        }

        var currencies = new HashSet<Currency>();
        entry.postings.forEach(p -> currencies.add(p.amount().currency()));
        if (currencies.size() != 1) {
            throw new ValidationException("All postings must have a single currency");
        }

        var debits = entry.getTotalDebits();
        var credits = entry.getTotalCredits();
        if (!debits.equals(credits)) {
            throw new ValidationException("Journal entry is not balances, debits=" + debits + ", credits=" + credits);
        }
    }
}
