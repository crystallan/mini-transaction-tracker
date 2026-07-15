# Implementation Plan - Complete AddTransactionViewModel

The goal is to complete the `AddTransactionViewModel` by implementing field-specific validation using a "draft" transaction and the existing `ValidateTransactionUseCase`.

## Proposed Changes

### [Component Name] Feature: Add Transaction

#### [MODIFY] [AddTransactionViewModel.kt](file:///C:/Users/Allan/AndroidStudioProjects/Mini_Transaction_Tracker/app/src/main/java/com/transaction/mini_transaction_tracker/feature/transaction/add/presentation/AddTransactionViewModel.kt)

- Complete `AddTransactionViewModel` class.
- Inject `ValidateTransactionUseCase`.
- Implement `onAmountChange`, `onDescriptionChange`, `onDateChange`, and `onTypeChange`.
- Implement a private `validateForm()` method that:
    - Creates a draft `Transaction` object from the current UI state.
    - Handles `BigDecimal` conversion for the amount (using `BigDecimal.ZERO` if parsing fails).
    - Calls `ValidateTransactionUseCase`.
    - Updates `amountError`, `descriptionError`, and `dateError` in `AddTransactionUiState`.
- Implement `submitTransaction()` method that:
    - Validates one last time.
    - Sets `isSubmitting` to true.
    - Calls `addTransactionUseCase`.
    - Handles success by setting `isSaved` to true.
    - Handles failure by updating a general error state or specific field errors.

## Verification Plan

### Automated Tests
- I will create a unit test for `AddTransactionViewModel` to verify that changing fields triggers validation and correctly maps errors to the UI state.

### Manual Verification
- Deploy the app and navigate to the "Add Transaction" screen.
- Enter invalid values (empty description, zero amount, future date) and verify errors appear.
- Enter valid values and verify the transaction is saved.
