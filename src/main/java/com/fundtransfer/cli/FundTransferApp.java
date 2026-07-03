package com.fundtransfer.cli;

import com.fundtransfer.model.Account;
import com.fundtransfer.model.Beneficiary;
import com.fundtransfer.repository.AccountRegistry;
import com.fundtransfer.service.DailyLimitTracker;
import com.fundtransfer.service.DefaultCreditService;
import com.fundtransfer.service.FundTransferService;
import com.fundtransfer.service.TransferValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FundTransferApp {

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();

        AccountRegistry registry = new AccountRegistry();
        registry.registerAccount(new Account("ACC-001", new BigDecimal("5000.00")));
        registry.registerAccount(new Account("ACC-002", new BigDecimal("1000.00")));
        registry.registerBeneficiary(new Beneficiary("BEN-001", "ACC-002", now.minusMinutes(5)));

        DailyLimitTracker tracker = new DailyLimitTracker();
        TransferValidator validator = new TransferValidator(tracker);
        FundTransferService service = new FundTransferService(
                registry, validator, tracker, new DefaultCreditService());

        System.out.println("Seeded accounts: ACC-001 (5000), ACC-002 (1000)");
        System.out.println("Seeded beneficiary: BEN-001 -> ACC-002 (eligible)\n");

        new FundTransferCli(registry, service, tracker).run();
    }
}
