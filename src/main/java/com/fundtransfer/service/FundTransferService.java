package com.fundtransfer.service;

import com.fundtransfer.exception.TransferFailedException;
import com.fundtransfer.model.Account;
import com.fundtransfer.model.Beneficiary;
import com.fundtransfer.model.TransferRequest;
import com.fundtransfer.repository.AccountRegistry;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FundTransferService {

    private final AccountRegistry registry;
    private final TransferValidator validator;
    private final DailyLimitTracker dailyLimitTracker;
    private final CreditService creditService;

    public FundTransferService(
            AccountRegistry registry,
            TransferValidator validator,
            DailyLimitTracker dailyLimitTracker,
            CreditService creditService) {
        this.registry = registry;
        this.validator = validator;
        this.dailyLimitTracker = dailyLimitTracker;
        this.creditService = creditService;
    }

    public void transfer(TransferRequest request, LocalDate transferDate) {
        Account source = registry.getAccount(request.getFromAccountId());
        Beneficiary beneficiary = registry.getBeneficiary(request.getBeneficiaryId());
        Account destination = registry.getAccount(beneficiary.getAccountId());

        validator.validate(request, source, beneficiary, transferDate);

        BigDecimal amount = request.getAmount();
        BigDecimal sourceBalanceBefore = source.getBalance();
        boolean debited = false;
        boolean credited = false;

        try {
            source.setBalance(sourceBalanceBefore.subtract(amount));
            debited = true;

            creditService.credit(destination, amount);
            credited = true;

            dailyLimitTracker.recordTransfer(source.getAccountId(), transferDate, amount);
        } catch (RuntimeException ex) {
            if (ex instanceof TransferFailedException) {
                throw ex;
            }
            throw new TransferFailedException("Transfer failed after debit; rolled back.", ex);
        } finally {
            if (debited && !credited) {
                source.setBalance(sourceBalanceBefore);
            }
        }
    }
}
