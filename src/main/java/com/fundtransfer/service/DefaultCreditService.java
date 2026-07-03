package com.fundtransfer.service;

import com.fundtransfer.model.Account;

import java.math.BigDecimal;

public class DefaultCreditService implements CreditService {

    @Override
    public void credit(Account destination, BigDecimal amount) {
        destination.setBalance(destination.getBalance().add(amount));
    }
}
