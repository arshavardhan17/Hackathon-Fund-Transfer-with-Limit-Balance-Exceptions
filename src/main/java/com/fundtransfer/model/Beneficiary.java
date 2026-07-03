package com.fundtransfer.model;

import java.time.LocalDate;
import java.util.Objects;

public class Beneficiary {

    private final String beneficiaryId;
    private final String accountId;
    private final LocalDate addedOn;

    public Beneficiary(String beneficiaryId, String accountId, LocalDate addedOn) {
        this.beneficiaryId = Objects.requireNonNull(beneficiaryId, "beneficiaryId");
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        this.addedOn = Objects.requireNonNull(addedOn, "addedOn");
    }

    public String getBeneficiaryId() {
        return beneficiaryId;
    }

    public String getAccountId() {
        return accountId;
    }

    public LocalDate getAddedOn() {
        return addedOn;
    }
}
