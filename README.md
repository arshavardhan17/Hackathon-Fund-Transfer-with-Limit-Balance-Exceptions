# PS-39 — Fund Transfer with Limit & Balance Exceptions

Banking fund-transfer CLI built in Java with custom exceptions, precondition validation, and (upcoming) atomic debit/credit transfers.

## Requirements

- Throw when daily transfer limit is exceeded
- Throw on insufficient balance **before** any debit
- Enforce a cooling period for newly added beneficiaries
- Guarantee debit and credit both succeed or both roll back *(Phase 3)*

## Tech Stack

- Java 17
- Maven (`pom.xml`)

## Project Structure

```
src/main/java/com/fundtransfer/
├── cli/           # CLI entry point
├── config/        # Transfer limits and cooling-period settings
├── exception/     # Custom exceptions
├── model/         # Account, Beneficiary, TransferRequest
└── service/       # Validation and transfer logic
```

## Build & Run

```bash
# Compile
javac -d target/classes $(find src/main/java -name "*.java")

# Run validation demo (Phase 2)
java -cp target/classes com.fundtransfer.cli.FundTransferApp
```

With Maven:

```bash
mvn compile exec:java -Dexec.mainClass=com.fundtransfer.cli.FundTransferApp
```

## Implementation Phases

| Phase | Status | Description |
|-------|--------|-------------|
| 1 | Done | Project skeleton, domain models, custom exceptions |
| 2 | Done | Precondition validators (balance, daily limit, cooling period) |
| 3 | Pending | Atomic transfer service with debit/credit rollback |
| 4 | Pending | Interactive CLI commands |

## Phase 2 — Validation Rules

All checks run **before** any account mutation.

| Rule | Exception | Config |
|------|-----------|--------|
| Amount must be positive | `IllegalArgumentException` | — |
| Sufficient balance | `InsufficientBalanceException` | — |
| Daily cumulative limit | `DailyLimitExceededException` | `10,000.00` per account per day |
| Beneficiary cooling period | `CoolingPeriodException` | `1` day after beneficiary is added |

### Cooling period

A beneficiary added on date `D` becomes eligible on `D + COOLING_PERIOD_DAYS`. Transfers attempted before that date are rejected.

### Daily limit tracking

`DailyLimitTracker` accumulates transfer amounts per account per calendar day. Each new transfer is validated against the limit **including** prior same-day transfers.

## Custom Exceptions

- `InsufficientBalanceException` — source account cannot cover the transfer
- `DailyLimitExceededException` — transfer would exceed the daily cap
- `CoolingPeriodException` — beneficiary is still within the cooling window

## Suggested Commits

```bash
# Phase 1
git commit -m "feat: add project foundation and domain models"

# Phase 2
git commit -m "feat: add transfer precondition validators"
```
