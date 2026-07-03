package com.fundtransfer.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DailyLimitTracker {

    private final Map<String, BigDecimal> dailyTotals = new HashMap<>();

    public BigDecimal getTransferredToday(String accountId, LocalDate date) {
        return dailyTotals.getOrDefault(key(accountId, date), BigDecimal.ZERO);
    }

    public void recordTransfer(String accountId, LocalDate date, BigDecimal amount) {
        dailyTotals.merge(key(accountId, date), amount, BigDecimal::add);
    }

    public BigDecimal remainingLimit(String accountId, LocalDate date, BigDecimal dailyLimit) {
        return dailyLimit.subtract(getTransferredToday(accountId, date));
    }

    private String key(String accountId, LocalDate date) {
        return accountId + "|" + date;
    }
}
