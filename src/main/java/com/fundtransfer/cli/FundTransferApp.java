package com.fundtransfer.cli;

import com.fundtransfer.exception.TransferFailedException;
import com.fundtransfer.model.Account;
import com.fundtransfer.model.Beneficiary;
import com.fundtransfer.model.TransferRequest;
import com.fundtransfer.repository.AccountRegistry;
import com.fundtransfer.service.DailyLimitTracker;
import com.fundtransfer.service.DefaultCreditService;
import com.fundtransfer.service.FundTransferService;
import com.fundtransfer.service.SimulatedCreditService;
import com.fundtransfer.service.TransferValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public class FundTransferApp {

    public static void main(String[] args) {
        LocalDate today = LocalDate.now();

        AccountRegistry registry = new AccountRegistry();
        registry.registerAccount(new Account("ACC-001", new BigDecimal("5000.00")));
        registry.registerAccount(new Account("ACC-002", new BigDecimal("1000.00")));
        registry.registerBeneficiary(new Beneficiary("BEN-001", "ACC-002", today.minusDays(2)));

        DailyLimitTracker tracker = new DailyLimitTracker();
        TransferValidator validator = new TransferValidator(tracker);
        FundTransferService service = new FundTransferService(
                registry,
                validator,
                tracker,
                new DefaultCreditService());

        TransferRequest success = new TransferRequest("ACC-001", "BEN-001", new BigDecimal("250.00"));
        service.transfer(success, today);

        Account source = registry.getAccount("ACC-001");
        Account destination = registry.getAccount("ACC-002");
        System.out.println("[OK] Successful transfer — source: " + source.getBalance() + ", dest: " + destination.getBalance());
        System.out.println("[OK] Daily total: " + tracker.getTransferredToday("ACC-001", today));

        AccountRegistry rollbackRegistry = new AccountRegistry();
        rollbackRegistry.registerAccount(new Account("ACC-001", new BigDecimal("5000.00")));
        rollbackRegistry.registerAccount(new Account("ACC-BLOCKED", new BigDecimal("0.00")));
        rollbackRegistry.registerBeneficiary(new Beneficiary("BEN-FAIL", "ACC-BLOCKED", today.minusDays(2)));

        DailyLimitTracker rollbackTracker = new DailyLimitTracker();
        FundTransferService rollbackService = new FundTransferService(
                rollbackRegistry,
                new TransferValidator(rollbackTracker),
                rollbackTracker,
                new SimulatedCreditService(new DefaultCreditService(), Set.of("ACC-BLOCKED")));

        BigDecimal balanceBefore = rollbackRegistry.getAccount("ACC-001").getBalance();
        try {
            rollbackService.transfer(
                    new TransferRequest("ACC-001", "BEN-FAIL", new BigDecimal("100.00")),
                    today);
        } catch (TransferFailedException ex) {
            BigDecimal balanceAfter = rollbackRegistry.getAccount("ACC-001").getBalance();
            System.out.println("[OK] Rollback on credit failure — balance restored: " + balanceBefore.equals(balanceAfter));
            System.out.println("[OK] Daily total unchanged: " + rollbackTracker.getTransferredToday("ACC-001", today));
        }
    }
}
