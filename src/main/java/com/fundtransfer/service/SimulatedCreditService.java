package com.fundtransfer.service;

import com.fundtransfer.exception.TransferFailedException;
import com.fundtransfer.model.Account;

import java.math.BigDecimal;
import java.util.Set;

public class SimulatedCreditService implements CreditService {

    private final CreditService delegate;
    private final Set<String> failingAccountIds;

    public SimulatedCreditService(CreditService delegate, Set<String> failingAccountIds) {
        this.delegate = delegate;
        this.failingAccountIds = failingAccountIds;
    }

    @Override
    public void credit(Account destination, BigDecimal amount) {
        if (failingAccountIds.contains(destination.getAccountId())) {
            throw new TransferFailedException("Simulated credit failure for account " + destination.getAccountId());
        }
        delegate.credit(destination, amount);
    }
}
