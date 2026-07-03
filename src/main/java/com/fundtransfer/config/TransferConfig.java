package com.fundtransfer.config;

import java.math.BigDecimal;

public final class TransferConfig {

    public static final BigDecimal DAILY_TRANSFER_LIMIT = new BigDecimal("10000.00");
    public static final int COOLING_PERIOD_DAYS = 1;

    private TransferConfig() {
    }
}
