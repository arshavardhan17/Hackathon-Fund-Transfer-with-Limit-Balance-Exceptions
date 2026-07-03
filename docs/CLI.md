# Usage Guide

## 1. Build and run

```bash
cd Hackathon-Fund-Transfer-with-Limit-Balance-Exceptions
javac -d target/classes $(find src/main/java -name "*.java")
java -cp target/classes com.fundtransfer.cli.FundTransferApp
```

Expected startup:

```
Seeded accounts: ACC-001 (5000), ACC-002 (1000)
Seeded beneficiary: BEN-001 -> ACC-002 (eligible)

Fund Transfer CLI
Daily limit: 10000.00 | Cooling period: 30 second(s)
Type 'help' for commands.

>
```

Type commands at the `>` prompt.

---

## 2. Concepts

| Term | Meaning |
|------|---------|
| **Account** | Holds a balance (e.g. `ACC-001`) |
| **Beneficiary** | A saved payee linked to a destination account (e.g. `BEN-001` → `ACC-002`) |
| **Transfer** | Move money from your account to a beneficiary's account |

You transfer **to a beneficiary**, not directly to an account ID.

```
ACC-001  ──via BEN-001──►  ACC-002
(source)                  (destination)
```

---

## 3. Commands

| Command | Usage | Description |
|---------|-------|-------------|
| `balance` | `balance <accountId>` | Show account balance |
| `add-beneficiary` | `add-beneficiary <id> <targetAccountId>` | Register payee (30s cooling starts now) |
| `transfer` | `transfer <from> <beneficiaryId> <amount>` | Send money |
| `daily` | `daily <accountId>` | Today's transferred total + remaining limit |
| `help` | `help` | List commands |
| `exit` | `exit` | Quit |

---

## 4. Create accounts

Accounts are **not** created from the CLI. They are registered at startup in:

`src/main/java/com/fundtransfer/cli/FundTransferApp.java`

```java
registry.registerAccount(new Account("ACC-003", new BigDecimal("2000.00")));
registry.registerAccount(new Account("ALICE", new BigDecimal("10000.00")));
```

Then recompile and run:

```bash
javac -d target/classes $(find src/main/java -name "*.java")
java -cp target/classes com.fundtransfer.cli.FundTransferApp
```

Data is in-memory only — nothing is saved after you exit.

---

## 5. Add beneficiaries

```
> add-beneficiary BEN-002 ACC-002
Beneficiary BEN-002 added. Eligible from 2026-07-04T00:26:25.
```

- `BEN-002` — your label for this payee
- `ACC-002` — destination account (must already exist)
- **Cooling period:** 30 seconds — transfers blocked until `Eligible from` time

To skip cooling for demo seed data, register with a past timestamp in code:

```java
registry.registerBeneficiary(new Beneficiary("BEN-001", "ACC-002", LocalDateTime.now().minusMinutes(5)));
```

---

## 6. Transfer money

```
> transfer ACC-001 BEN-001 250
Transfer complete.
  Source balance: 4750.00
  Destination balance: 1250.00
  Transferred today: 250
```

Before any debit, the app checks:

1. Amount is positive
2. Source has enough balance
3. Daily limit not exceeded
4. Beneficiary cooling period has passed

If any check fails, **no money moves**.

---

## 7. Check cooling period

There is no separate `cooling` command. You see it when:

**Adding a beneficiary** — shows eligible time:
```
Beneficiary BEN-NEW added. Eligible from 2026-07-04T00:26:55.
```

**Transferring too early** — rejection message:
```
Transfer rejected: Beneficiary BEN-NEW is still in cooling period. Eligible from: ..., attempted at: ...
```

**Rule:** eligible at `addedAt + 30 seconds`

Demo flow:
```
> add-beneficiary BEN-X ACC-002
> transfer ACC-001 BEN-X 100          # rejected
(wait 30 seconds)
> transfer ACC-001 BEN-X 100          # succeeds
```

Config: `src/main/java/com/fundtransfer/config/TransferConfig.java` → `COOLING_PERIOD_SECONDS`

---

## 8. Check daily limit

```
> daily ACC-001
ACC-001 — transferred today: 250, remaining: 9750.00
```

Limit is **10,000.00** per account per calendar day. Multiple transfers add up.

---

## 9. Full walkthrough

```
> balance ACC-001
ACC-001 balance: 5000.00

> balance ACC-002
ACC-002 balance: 1000.00

> transfer ACC-001 BEN-001 250
Transfer complete.
  Source balance: 4750.00
  Destination balance: 1250.00
  Transferred today: 250

> add-beneficiary BEN-NEW ACC-002
Beneficiary BEN-NEW added. Eligible from ...

> transfer ACC-001 BEN-NEW 100
Transfer rejected: Beneficiary BEN-NEW is still in cooling period...

> daily ACC-001
ACC-001 — transferred today: 250, remaining: 9750.00

> exit
Goodbye.
```

---

## 10. Demo scenarios (hackathon)

| # | What to show | Command |
|---|--------------|---------|
| 1 | Successful transfer | `transfer ACC-001 BEN-001 250` |
| 2 | Cooling period | Add beneficiary → transfer now (fail) → wait 30s → transfer (ok) |
| 3 | Insufficient balance | `transfer ACC-001 BEN-001 99999` |
| 4 | Daily limit | Multiple transfers totalling over 10,000 |

---

## 11. Common errors

| Error | Cause |
|-------|-------|
| `Account not found` | Wrong account ID or not registered in `FundTransferApp.java` |
| `Beneficiary not found` | Wrong beneficiary ID or not added yet |
| `Insufficient balance` | Source account has less than transfer amount |
| `Daily transfer limit exceeded` | Same-day total would go over 10,000 |
| `Cooling period` | Beneficiary added less than 30 seconds ago |
| `Transfer amount must be positive` | Amount is 0 or negative |

---

## 12. Project layout

```
src/main/java/com/fundtransfer/
├── cli/FundTransferApp.java      # Entry point + seed data
├── cli/FundTransferCli.java    # Interactive commands
├── config/TransferConfig.java  # Limits (daily, cooling)
├── exception/                  # Custom exceptions
├── model/                      # Account, Beneficiary, TransferRequest
├── repository/                 # In-memory account store
└── service/                    # Validation + atomic transfer
```
