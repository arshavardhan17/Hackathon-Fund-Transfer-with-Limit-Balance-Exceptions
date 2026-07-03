package com.fundtransfer.repository;

import com.fundtransfer.model.Account;
import com.fundtransfer.model.Beneficiary;

import java.util.HashMap;
import java.util.Map;

public class AccountRegistry {

    private final Map<String, Account> accounts = new HashMap<>();
    private final Map<String, Beneficiary> beneficiaries = new HashMap<>();

    public void registerAccount(Account account) {
        accounts.put(account.getAccountId(), account);
    }

    public void registerBeneficiary(Beneficiary beneficiary) {
        beneficiaries.put(beneficiary.getBeneficiaryId(), beneficiary);
    }

    public Account getAccount(String accountId) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }
        return account;
    }

    public Beneficiary getBeneficiary(String beneficiaryId) {
        Beneficiary beneficiary = beneficiaries.get(beneficiaryId);
        if (beneficiary == null) {
            throw new IllegalArgumentException("Beneficiary not found: " + beneficiaryId);
        }
        return beneficiary;
    }
}
