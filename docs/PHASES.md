# Implementation Phases

## Phase 1 — Foundation

- Maven project, Java 17
- Domain models: `Account`, `Beneficiary`, `TransferRequest`
- Custom exceptions for business rule violations

**Commit:** `feat: add project foundation and domain models`

## Phase 2 — Validation

- `TransferValidator` — all checks before mutation
- `DailyLimitTracker` — per-account, per-day cumulative totals
- `TransferConfig` — limit and cooling-period constants

**Commit:** `feat: add transfer precondition validators`

## Phase 3 — Atomic transfer

- `FundTransferService` — validate → debit → credit → record limit
- Rollback on credit failure (`catch` + `finally`)
- `AccountRegistry`, `CreditService`, `TransferFailedException`

**Commit:** `feat: implement atomic fund transfer with rollback`

## Phase 4 — Interactive CLI

- `FundTransferCli` — command loop: balance, add-beneficiary, transfer, daily, exit
- Seeded demo accounts on startup
- See [CLI.md](CLI.md) for commands and demo scenarios

**Commit:** `feat: add interactive CLI for fund transfers`
