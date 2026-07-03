# CLI Reference

## Commands

| Command | Description |
|---------|-------------|
| `balance <accountId>` | Show account balance |
| `add-beneficiary <id> <targetAccountId>` | Register beneficiary (cooling period starts today) |
| `transfer <from> <beneficiaryId> <amount>` | Atomic fund transfer |
| `daily <accountId>` | Show today's transferred amount and remaining limit |
| `help` | List commands |
| `exit` | Quit |

## Sample session

```
> balance ACC-001
ACC-001 balance: 5000.00

> transfer ACC-001 BEN-001 250
Transfer complete.
  Source balance: 4750.00
  Destination balance: 1250.00
  Transferred today: 250.00

> add-beneficiary BEN-NEW ACC-002
Beneficiary BEN-NEW added. Eligible from 2026-07-04.

> transfer ACC-001 BEN-NEW 100
Transfer rejected: Beneficiary BEN-NEW is still in cooling period...

> daily ACC-001
ACC-001 — transferred today: 250.00, remaining: 9750.00
```

## Demo scenarios (for judges)

1. **Successful transfer** — `transfer ACC-001 BEN-001 250`
2. **Cooling period** — add a beneficiary today, try transferring immediately
3. **Insufficient balance** — `transfer ACC-001 BEN-001 99999`
4. **Daily limit** — multiple transfers totalling over 10,000
