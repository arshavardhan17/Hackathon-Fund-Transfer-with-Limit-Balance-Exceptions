package com.fundtransfer.service;

import com.fundtransfer.model.Account;

import java.math.BigDecimal;

public interface CreditService {

    void credit(Account destination, BigDecimal amount);
}
