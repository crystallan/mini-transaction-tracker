package com.transaction.shared.presentation.transaction.add.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transaction.shared.domain.model.Transaction
import com.transaction.shared.domain.model.TransactionType
import com.transaction.shared.domain.usecases.AddTransactionUseCase
import com.transaction.shared.domain.usecases.ValidateTransactionUseCase
import com.transaction.shared.domain.model.ValidationField
import com.transaction.shared.domain.model.ValidationResult
import com.transaction.shared.domain.utils.toDecimalOrNull
import com.transaction.shared.utils.AmountInput
import io.github.kormium.decimal.Decimal
import io.github.kormium.decimal.RoundingMode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock

data class AddTransactionUiState(
    val amount: String = "",
    val description: String = "",
    val date: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val type: TransactionType = TransactionType.DEBIT,
    val amountError: String? = null,
    val descriptionError: String? = null,
    val dateError: String? = null,
    val isSubmitting: Boolean = false,
)

sealed interface AddTransactionViewEvent {
    data object Success : AddTransactionViewEvent
    data class Error(val message: String) : AddTransactionViewEvent
}

class AddTransactionViewModel(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val validateTransactionUseCase: ValidateTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<AddTransactionViewEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onAmountChange(amount: String) {
        // Invalid edits are dropped, so the field keeps its last valid value
        val sanitized = AmountInput.sanitize(amount) ?: return
        _uiState.update { it.copy(amount = sanitized, amountError = null) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description, descriptionError = null) }
    }

    fun onDateChange(date: LocalDateTime) {
        _uiState.update { it.copy(date = date, dateError = null) }
    }

    fun onTypeChange(type: TransactionType) {
        _uiState.update { it.copy(type = type) }
    }

    fun validateForm(): Decimal? {
        val currentState = _uiState.value
        val amountText = AmountInput.finalize(currentState.amount) // "12." -> "12"
        val result = validateTransactionUseCase(
            amountText,
            currentState.description,
            currentState.date
        )

        _uiState.update { state ->
            when (result) {
                is ValidationResult.Valid -> state.copy(
                    amountError = null,
                    descriptionError = null,
                    dateError = null
                )
                is ValidationResult.Invalid -> {
                    val errorsByField = result.errors.associateBy { it.field }
                    state.copy(
                        amountError = errorsByField[ValidationField.AMOUNT]?.message,
                        descriptionError = errorsByField[ValidationField.DESCRIPTION]?.message,
                        dateError = errorsByField[ValidationField.DATE]?.message
                    )
                }
            }
        }

        if (result !is ValidationResult.Valid) return null

        val amount = amountText.toDecimalOrNull()?.setScale(2, RoundingMode.HALF_UP)
        if (amount == null) {
            // Validation passed but parsing failed: show it instead of making Save do nothing
            _uiState.update { it.copy(amountError = "Enter a valid amount") }
        }
        return amount
    }
    fun submitTransaction() {
        if (_uiState.value.isSubmitting) return
        val parsedAmount = validateForm() ?: return

        // Snapshot the exact values that were validated; later edits must not change what's saved
        val validatedState = _uiState.value
        // Set before launching: a fast double-tap can't start a second save
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            try {
                val state = validatedState
                val transaction = Transaction(
                    amount = parsedAmount,
                    description = state.description,
                    date = state.date,
                    type = state.type
                )
                addTransactionUseCase(transaction)
                _eventChannel.send(AddTransactionViewEvent.Success)
            } catch (e: CancellationException) {
                throw e // leaving the screen cancels the save; don't report it as an error
            } catch (e: Exception) {
                _eventChannel.send(AddTransactionViewEvent.Error(e.message ?: "An unexpected error occurred"))
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }
}