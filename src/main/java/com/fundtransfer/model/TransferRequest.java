package com.fundtransfer.model;

import java.math.BigDecimal;
import java.util.Objects;

public class TransferRequest {

    private final String fromAccountId;
    private final String beneficiaryId;
    private final BigDecimal amount;

    public TransferRequest(String fromAccountId, String beneficiaryId, BigDecimal amount) {
        this.fromAccountId = Objects.requireNonNull(fromAccountId, "fromAccountId");
        this.beneficiaryId = Objects.requireNonNull(beneficiaryId, "beneficiaryId");
        this.amount = Objects.requireNonNull(amount, "amount");
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public String getBeneficiaryId() {
        return beneficiaryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
