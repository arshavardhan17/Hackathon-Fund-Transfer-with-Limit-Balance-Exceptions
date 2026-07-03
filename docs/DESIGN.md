# Design

## Transfer flow

```
validate (no state change)
    ↓
debit source
    ↓
credit destination  ──fail──► rollback debit (catch + finally)
    ↓
record daily total
```

## Validation (before debit)

| Rule | Exception |
|------|-----------|
| Positive amount | `IllegalArgumentException` |
| Sufficient balance | `InsufficientBalanceException` |
| Daily limit (cumulative) | `DailyLimitExceededException` |
| Beneficiary cooling period | `CoolingPeriodException` |

Config defaults (`TransferConfig`):

- Daily limit: **10,000.00** per account per calendar day
- Cooling period: **1 day** after beneficiary is added (eligible from `addedOn + 1`)

## Atomicity

`FundTransferService` debits first, then credits. If credit fails:

1. `catch` restores the source balance
2. `finally` acts as a safety net if rollback was missed
3. Daily limit is **not** updated unless both steps succeed

## Exceptions

| Class | When |
|-------|------|
| `InsufficientBalanceException` | Balance check fails |
| `DailyLimitExceededException` | Same-day total would exceed cap |
| `CoolingPeriodException` | Beneficiary not yet eligible |
| `TransferFailedException` | Credit step fails; debit rolled back |

## Package layout

```
com.fundtransfer
├── cli          FundTransferApp
├── config       TransferConfig
├── exception    custom exceptions
├── model        Account, Beneficiary, TransferRequest
├── repository   AccountRegistry
└── service      validator, tracker, transfer service, credit
```
