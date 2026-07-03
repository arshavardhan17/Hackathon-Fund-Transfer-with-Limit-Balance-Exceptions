# PS-39 — Fund Transfer with Limit & Balance Exceptions

Java CLI for atomic fund transfers with daily limits, beneficiary cooling periods, and custom exceptions.

## Run

```bash
javac -d target/classes $(find src/main/java -name "*.java")
java -cp target/classes com.fundtransfer.cli.FundTransferApp
```

## Status

| Phase | Done |
|-------|------|
| 1 — Models & exceptions | ✓ |
| 2 — Precondition validation | ✓ |
| 3 — Atomic transfer + rollback | ✓ |
| 4 — Interactive CLI | ✓ |

## Docs

- [CLI commands](docs/CLI.md)
- [Phase breakdown](docs/PHASES.md)
