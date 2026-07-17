# Mini Transaction Tracker

# Allan Korir

A local-first personal wallet ledger for tracking money in and out of an account. Built for Kocela's Mobile Application Developer take-home task.

---

## Tech Stack

- Kotlin, Jetpack Compose (Material 3)
- Room (local persistence)
- Koin (dependency injection)
- Kotlin Coroutines + Flow
- Navigation Compose

---

## Features

### Core (required by brief)
- [x] Transaction list — amount, description, date, type
- [x] Add transaction with adequate validation - positive amount, required description, date not in the future
- [x] Transaction detail view
- [x] Local persistence via Room local storage to survive app restart
- [x] Running balance, calculated reactively

### Extras (implemented)
- [x] Search/filter by keyword, type, and date range
- [x] Sort by date, amount, or description (ascending/descending)
- [x] Currency display toggle (KES ⇄ USD, display-only conversion)
- [x] Negative balance warning
- [x] Delete with confirmation dialog
- [x] Seeded sample data on first launch

---

## How to Run

1. Clone the repo: `git clone https://github.com/crystallan/mini-transaction-tracker`
2. Open in Android Studio Version 26.1.2 (Quail 2)
3. Let Gradle sync
4. Build → Select Build Variant in Android Studio → change `app` from `debug` to `release`
5. Run on an emulator or physical device (**min SDK 24**)
6. The app seeds the sample transactions from `assets/sample_transactions.json` on first launch

Note: Release build variants reduce overhead and run smoother than debug builds. This project currently builds with the default debug keystore which is acceptable for development.

---
## Architecture

The app follows a layered structure: Compose screens → ViewModels → domain use cases → a repository interface → a Room-backed implementation. The domain layer has no Android/Room dependency. Koin wires everything together at startup.

```
core/
  data/
    local/         → Room (Entity, Dao, Database, Converters, mappers)
    repository/    → TransactionRepositoryImpl
  domain/
    model/         → Transaction, TransactionType, ValidationResult, ValidationField, FieldError
    repository/    → TransactionRepository (interface)
    usecase/       → one class per operation
    utils/         → TransactionOrder, OrderType, TransactionFilter
  seed/            → SeedDataSource, SeedDataDto
  utils/           → CurrencyUtils, DateUtils

feature/
  transaction/
    add/presentation/     → AddTransactionScreen, AddTransactionViewModel
    view/presentation/    → ViewTransactionScreen, ViewTransactionViewModel
    detail/presentation/  → TransactionDetailScreen, TransactionDetailViewModel

navigation/    → AppDestinations, NavGraph
di/            → AppModule, DataBaseModule, TransactionModule
```
<img width="8192" height="4859" alt="Architecture" src="https://github.com/user-attachments/assets/e846f437-3e4c-43d5-b424-c79234ddb06b" />

<p align="center"><sub><b>Fig 1.</b> Layered architecture — presentation → domain → data</sub></p>

## Data Model

The persistence layer is intentionally a single table — `TransactionEntity` — since a personal ledger has no relational structure to model. `TransactionType` is the enum that shapes the `type` column; `ValidationField`, `TransactionFilter`, and `TransactionOrder` are transient value objects that shape reads/writes, not separate persisted tables.

<img width="2612" height="1272" alt="ERD" src="https://github.com/user-attachments/assets/dd6e8e0b-56da-4f09-a1b9-d11f42fe8159" />

<p align="center"><sub><b>Fig 2.</b> Entity-relationship diagram — single-table schema</sub></p>

## Navigation

| Route | Screen | Entry point | Exit point |
|---|---|---|---|
| `home` | `ViewTransactionScreen` | App launch | — |
| `add` | `AddTransactionScreen` | FAB tap on Home | Save success → back to Home |
| `detail/{transactionId}` | `TransactionDetailScreen` | Row tap on Home | Back arrow → Home |

<img width="3650" height="953" alt="Nav Flow" src="https://github.com/user-attachments/assets/06aeb2e5-f2e1-4696-9c31-2f6930e66238" />

<p align="center"><sub><b>Fig 3.</b> Screen navigation graph — three destinations, single NavHost</sub></p>

## Key Flows

### Add Transaction
Two-layer validation: live validation in the ViewModel as the user types, plus a defensive re-validation inside `AddTransactionUseCase` before the actual database write.

**Validation rules:**
- Amount must parse as a number and be greater than 0
- Description must not be blank
- Date must not be in the future

<img width="8192" height="4158" alt="Sequence - Adding a Transaction" src="https://github.com/user-attachments/assets/7841508d-d4c2-4f4d-9401-7ba65f8e5963" />


### Reactive List & Balance Sync
The transaction list and running balance both subscribe to the same Room Flow. So when anything changes Room emits automatically and both refresh on their own. No manual reload logic needed.


All three checks run together and return every applicable error at once

**Balance:** sum of credits minus sum of debits, recalculated reactively on every insert/delete (see Reactive List & Balance Sync above).

**Credit/debit convention:** follows standard bank-statement double-entry convention.

<img width="8192" height="4396" alt="Sequence - Balance Calculation" src="https://github.com/user-attachments/assets/179ea1d1-a003-47db-b797-d1be94118cc9" />

<p align="center"><sub><b>Fig 5.</b> Sequence diagram — reactive list/balance sync via Room's Flow</sub></p>

## Decisions & Trade-offs

- **Clean Layered architecture** — chosen for consistency and alignment with production grade systems
- **In-memory filtering + sorting + balance calculation** - The relatively small size of the dataset allowed for in Memory processes - if presented with a larger data set I would move to SQL @Query
- **`BigDecimal` + text storage for amounts** — Precision and accuracy for monetary transactions - ie rounding off and decimals
- **Flat KES→USD conversion rate, display-only** — Due to time constraints currency conversion happens with a hardcoded conversion rate 'KES_TO_USD_RATE = BigDecimal("0.01")' - 1 KES = 0.01 USD 
- **Koin over Hilt For Dependency Injection** - Familiarity with Koin and lower overhead as compared with Dagger Hilt
  

---

## What I'd Add With More Time

1. **Unit tests** — Specifically around the Use Cases to verify their correctness
2. **At-rest encryption** — Encryption of the room database by using SQLCipher with a generated keys stored in the android keystore for safety
3. **Move balance to a SQL `SUM` aggregate** 
4. **Move filtering to SQL-level queries** - If provided with a larger dataset
5. **Edit-transaction flow** 
6. **`SavedStateHandle`** on `AddTransactionViewModel` so in-progress form input survives process death
7. **Room migration strategy** — currently schema version 1, no upgrade path defined yet
8. **Mock API handling** - Use of RetroFit and OkHttp to simulate how an api request and response would be handled
9. **Figma Mockups** - To directly guide the development of the UI
10. **Better/cleaner UI creation**

---

## Appendix: Additional Diagrams

These weren't essential to the core README narrative above but are included for completeness.

<table>
  <tr>
    <td align="center" width="50%">
      <img width="100%" alt="Validating state diagram for Add Transaction submission" src="https://github.com/user-attachments/assets/42cecdea-ed24-4ca1-b378-e737109c2409" /><br />
      <sub><b>State diagram</b> — Add Transaction submission lifecycle (idle → validating → invalid/submitting → success/error)</sub>
    </td>
    <td align="center" width="50%">
      <img width="100%" alt="Class diagram of domain and data layer relationships" src="https://github.com/user-attachments/assets/eb3a2296-8467-49d0-83dd-d3d068ad5a3c" /><br />
      <sub><b>Class diagram</b> — domain/data class relationships</sub>
    </td>
  </tr>
  <tr>
    <td align="center" width="50%">
      <img width="100%" alt="Dependency injection flowchart showing Koin modules" src="https://github.com/user-attachments/assets/a80b8674-944a-4a11-923b-0e6a5b1f97e1" /><br />
      <sub><b>DI graph</b> — Koin module wiring (DataBaseModule, TransactionModule)</sub>
    </td>
    <td align="center" width="50%">
      <img width="100%" alt="Sequence diagram for app first launch and database seeding" src="https://github.com/user-attachments/assets/d8d191f8-3f35-4306-b2dc-983b1c5f3c61" /><br />
      <sub><b>Sequence</b> — app first-launch startup & database seeding</sub>
    </td>
  </tr>
  <tr>
    <td align="center" colspan="2">
      <img width="60%" alt="Sequence diagram for deleting a transaction" src="https://github.com/user-attachments/assets/c92b4194-6ead-4500-b1b6-e7a853c62e9a" /><br />
      <sub><b>Sequence</b> — deleting a transaction, showing confirmation dialog and reactive re-sync</sub>
    </td>
  </tr>
</table>
