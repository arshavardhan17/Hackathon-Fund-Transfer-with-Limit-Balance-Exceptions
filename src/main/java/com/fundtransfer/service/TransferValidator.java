package com.fundtransfer.service;

import com.fundtransfer.config.TransferConfig;
import com.fundtransfer.exception.CoolingPeriodException;
import com.fundtransfer.exception.DailyLimitExceededException;
import com.fundtransfer.exception.InsufficientBalanceException;
import com.fundtransfer.model.Account;
import com.fundtransfer.model.Beneficiary;
import com.fundtransfer.model.TransferRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransferValidator {

    private final DailyLimitTracker dailyLimitTracker;
    private final BigDecimal dailyLimit;
    private final int coolingPeriodSeconds;

    public TransferValidator(DailyLimitTracker dailyLimitTracker) {
        this(dailyLimitTracker, TransferConfig.DAILY_TRANSFER_LIMIT, TransferConfig.COOLING_PERIOD_SECONDS);
    }

    public TransferValidator(DailyLimitTracker dailyLimitTracker, BigDecimal dailyLimit, int coolingPeriodSeconds) {
        this.dailyLimitTracker = dailyLimitTracker;
        this.dailyLimit = dailyLimit;
        this.coolingPeriodSeconds = coolingPeriodSeconds;
    }

    public void validate(TransferRequest request, Account source, Beneficiary beneficiary, LocalDateTime transferTime) {
        validateAmount(request.getAmount());
        validateBalance(source, request.getAmount());
        validateDailyLimit(source.getAccountId(), transferTime.toLocalDate(), request.getAmount());
        validateCoolingPeriod(beneficiary, transferTime);
    }

    public void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
    }

    public void validateBalance(Account source, BigDecimal amount) {
        if (source.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance in account " + source.getAccountId()
                            + ". Available: " + source.getBalance() + ", required: " + amount);
        }
    }

    public void validateDailyLimit(String accountId, LocalDate transferDate, BigDecimal amount) {
        BigDecimal alreadyTransferred = dailyLimitTracker.getTransferredToday(accountId, transferDate);
        BigDecimal projectedTotal = alreadyTransferred.add(amount);

        if (projectedTotal.compareTo(dailyLimit) > 0) {
            throw new DailyLimitExceededException(
                    "Daily transfer limit exceeded for account " + accountId
                            + ". Limit: " + dailyLimit
                            + ", already transferred today: " + alreadyTransferred
                            + ", requested: " + amount);
        }
    }

    public void validateCoolingPeriod(Beneficiary beneficiary, LocalDateTime transferTime) {
        LocalDateTime eligibleFrom = beneficiary.getAddedAt().plusSeconds(coolingPeriodSeconds);

        if (transferTime.isBefore(eligibleFrom)) {
            throw new CoolingPeriodException(
                    "Beneficiary " + beneficiary.getBeneficiaryId()
                            + " is still in cooling period. Eligible from: " + eligibleFrom
                            + ", attempted at: " + transferTime);
        }
    }
}
