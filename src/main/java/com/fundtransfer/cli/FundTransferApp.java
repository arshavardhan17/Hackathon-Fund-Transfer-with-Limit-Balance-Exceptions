package com.fundtransfer.cli;

import com.fundtransfer.exception.CoolingPeriodException;
import com.fundtransfer.exception.DailyLimitExceededException;
import com.fundtransfer.exception.InsufficientBalanceException;
import com.fundtransfer.model.Account;
import com.fundtransfer.model.Beneficiary;
import com.fundtransfer.model.TransferRequest;
import com.fundtransfer.service.DailyLimitTracker;
import com.fundtransfer.service.TransferValidator;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FundTransferApp {

    public static void main(String[] args) {
        DailyLimitTracker tracker = new DailyLimitTracker();
        TransferValidator validator = new TransferValidator(tracker);

        Account source = new Account("ACC-001", new BigDecimal("5000.00"));
        Beneficiary beneficiary = new Beneficiary("BEN-001", "ACC-002", LocalDate.now());
        TransferRequest request = new TransferRequest("ACC-001", "BEN-001", new BigDecimal("100.00"));

        runValidation("Cooling period check", () ->
                validator.validate(request, source, beneficiary, LocalDate.now()));

        runValidation("Insufficient balance check", () -> {
            Beneficiary eligible = new Beneficiary("BEN-002", "ACC-002", LocalDate.now().minusDays(2));
            TransferRequest large = new TransferRequest("ACC-001", "BEN-002", new BigDecimal("6000.00"));
            validator.validate(large, source, eligible, LocalDate.now());
        });

        tracker.recordTransfer("ACC-001", LocalDate.now(), new BigDecimal("9500.00"));
        runValidation("Daily limit check", () -> {
            Beneficiary eligible = new Beneficiary("BEN-003", "ACC-002", LocalDate.now().minusDays(2));
            TransferRequest overLimit = new TransferRequest("ACC-001", "BEN-003", new BigDecimal("600.00"));
            validator.validate(overLimit, source, eligible, LocalDate.now());
        });

        System.out.println("Phase 2 validation checks complete.");
    }

    private static void runValidation(String label, Runnable check) {
        try {
            check.run();
            System.out.println("[PASS] " + label);
        } catch (InsufficientBalanceException | DailyLimitExceededException | CoolingPeriodException ex) {
            System.out.println("[EXPECTED] " + label + " -> " + ex.getClass().getSimpleName());
        }
    }
}
