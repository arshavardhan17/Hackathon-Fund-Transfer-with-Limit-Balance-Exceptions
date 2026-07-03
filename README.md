# PS-39 — Fund Transfer with Limit & Balance Exceptions

Java CLI for atomic fund transfers with daily limits, beneficiary cooling periods, and custom exceptions.

## Prerequisites

- Java 17 or later (`java -version`)
- Terminal

## Run

```bash
# 1. Go to project folder
cd Hackathon-Fund-Transfer-with-Limit-Balance-Exceptions

# 2. Compile
javac -d target/classes $(find src/main/java -name "*.java")

# 3. Start the app
java -cp target/classes com.fundtransfer.cli.FundTransferApp
```

With Maven (if installed):

```bash
mvn compile exec:java -Dexec.mainClass=com.fundtransfer.cli.FundTransferApp
```

## Quick start

On startup you get two accounts and one eligible beneficiary:

| Item | Details |
|------|---------|
| `ACC-001` | Balance 5000 — use as source |
| `ACC-002` | Balance 1000 — receives money |
| `BEN-001` | Points to `ACC-002`, already eligible |

Try this after the `>` prompt appears:

```
balance ACC-001
transfer ACC-001 BEN-001 250
daily ACC-001
exit
```

## Commands

| Command | Example |
|---------|---------|
| Check balance | `balance ACC-001` |
| Add beneficiary | `add-beneficiary BEN-002 ACC-002` |
| Transfer | `transfer ACC-001 BEN-001 250` |
| Daily limit usage | `daily ACC-001` |
| Help | `help` |
| Quit | `exit` |

## Rules

- **Daily limit:** 10,000 per account per day
- **Cooling period:** 30 seconds after adding a beneficiary
- **Transfers:** debit and credit succeed together, or neither does

## Create accounts

There is no `add-account` CLI command. Add accounts in `FundTransferApp.java`:

```java
registry.registerAccount(new Account("ACC-003", new BigDecimal("2000.00")));
```

Recompile and run again.

## Docs

- [Full usage guide](docs/CLI.md) — commands, scenarios, errors
- [Design](docs/DESIGN.md) — validation and atomic transfer flow
- [Phases](docs/PHASES.md) — implementation breakdown
