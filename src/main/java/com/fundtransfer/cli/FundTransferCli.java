package com.fundtransfer.cli;

import com.fundtransfer.config.TransferConfig;
import com.fundtransfer.exception.CoolingPeriodException;
import com.fundtransfer.exception.DailyLimitExceededException;
import com.fundtransfer.exception.InsufficientBalanceException;
import com.fundtransfer.exception.TransferFailedException;
import com.fundtransfer.model.Account;
import com.fundtransfer.model.Beneficiary;
import com.fundtransfer.model.TransferRequest;
import com.fundtransfer.repository.AccountRegistry;
import com.fundtransfer.service.DailyLimitTracker;
import com.fundtransfer.service.FundTransferService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

public class FundTransferCli {

    private final AccountRegistry registry;
    private final FundTransferService transferService;
    private final DailyLimitTracker dailyLimitTracker;
    private final LocalDate today;

    public FundTransferCli(
            AccountRegistry registry,
            FundTransferService transferService,
            DailyLimitTracker dailyLimitTracker,
            LocalDate today) {
        this.registry = registry;
        this.transferService = transferService;
        this.dailyLimitTracker = dailyLimitTracker;
        this.today = today;
    }

    public void run() {
        printBanner();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }

            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\s+");
            String command = parts[0].toLowerCase();

            try {
                switch (command) {
                    case "help" -> printHelp();
                    case "exit", "quit" -> {
                        System.out.println("Goodbye.");
                        return;
                    }
                    case "balance" -> handleBalance(parts);
                    case "add-beneficiary" -> handleAddBeneficiary(parts);
                    case "transfer" -> handleTransfer(parts);
                    case "daily" -> handleDaily(parts);
                    default -> System.out.println("Unknown command. Type 'help' for options.");
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (InsufficientBalanceException | DailyLimitExceededException
                     | CoolingPeriodException | TransferFailedException ex) {
                System.out.println("Transfer rejected: " + ex.getMessage());
            }
        }
    }

    private void printBanner() {
        System.out.println("Fund Transfer CLI");
        System.out.println("Daily limit: " + TransferConfig.DAILY_TRANSFER_LIMIT
                + " | Cooling period: " + TransferConfig.COOLING_PERIOD_DAYS + " day(s)");
        System.out.println("Type 'help' for commands.\n");
    }

    private void printHelp() {
        System.out.println("""
                Commands:
                  balance <accountId>
                  add-beneficiary <beneficiaryId> <targetAccountId>
                  transfer <fromAccountId> <beneficiaryId> <amount>
                  daily <accountId>
                  help
                  exit
                """);
    }

    private void handleBalance(String[] parts) {
        requireArgs(parts, 2, "balance <accountId>");
        String accountId = parts[1];
        System.out.println(accountId + " balance: " + registry.getAccount(accountId).getBalance());
    }

    private void handleAddBeneficiary(String[] parts) {
        requireArgs(parts, 3, "add-beneficiary <beneficiaryId> <targetAccountId>");
        String beneficiaryId = parts[1];
        String targetAccountId = parts[2];
        registry.getAccount(targetAccountId);

        Beneficiary beneficiary = new Beneficiary(beneficiaryId, targetAccountId, today);
        registry.registerBeneficiary(beneficiary);

        LocalDate eligibleFrom = today.plusDays(TransferConfig.COOLING_PERIOD_DAYS);
        System.out.println("Beneficiary " + beneficiaryId + " added. Eligible from " + eligibleFrom + ".");
    }

    private void handleTransfer(String[] parts) {
        requireArgs(parts, 4, "transfer <fromAccountId> <beneficiaryId> <amount>");
        String fromAccountId = parts[1];
        String beneficiaryId = parts[2];
        BigDecimal amount = new BigDecimal(parts[3]);

        TransferRequest request = new TransferRequest(fromAccountId, beneficiaryId, amount);
        transferService.transfer(request, today);

        Account beneficiaryAccount = registry.getAccount(registry.getBeneficiary(beneficiaryId).getAccountId());
        System.out.println("Transfer complete.");
        System.out.println("  Source balance: " + registry.getAccount(fromAccountId).getBalance());
        System.out.println("  Destination balance: " + beneficiaryAccount.getBalance());
        System.out.println("  Transferred today: " + dailyLimitTracker.getTransferredToday(fromAccountId, today));
    }

    private void handleDaily(String[] parts) {
        requireArgs(parts, 2, "daily <accountId>");
        String accountId = parts[1];
        BigDecimal transferred = dailyLimitTracker.getTransferredToday(accountId, today);
        BigDecimal remaining = dailyLimitTracker.remainingLimit(accountId, today, TransferConfig.DAILY_TRANSFER_LIMIT);
        System.out.println(accountId + " — transferred today: " + transferred + ", remaining: " + remaining);
    }

    private void requireArgs(String[] parts, int expected, String usage) {
        if (parts.length != expected) {
            throw new IllegalArgumentException("Usage: " + usage);
        }
    }
}
