package com.fundtransfer.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {

    private final String accountId;
    private BigDecimal balance;

    public Account(String accountId, BigDecimal balance) {
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        this.balance = Objects.requireNonNull(balance, "balance");
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = Objects.requireNonNull(balance, "balance");
    }
}
