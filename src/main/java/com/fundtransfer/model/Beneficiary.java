package com.fundtransfer.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Beneficiary {

    private final String beneficiaryId;
    private final String accountId;
    private final LocalDateTime addedAt;

    public Beneficiary(String beneficiaryId, String accountId, LocalDateTime addedAt) {
        this.beneficiaryId = Objects.requireNonNull(beneficiaryId, "beneficiaryId");
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        this.addedAt = Objects.requireNonNull(addedAt, "addedAt");
    }

    public String getBeneficiaryId() {
        return beneficiaryId;
    }

    public String getAccountId() {
        return accountId;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }
}
