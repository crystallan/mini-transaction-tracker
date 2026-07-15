package com.transaction.mini_transaction_tracker.feature.transaction.add.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transaction.mini_transaction_tracker.core.domain.model.Transaction
import com.transaction.mini_transaction_tracker.core.domain.model.TransactionType
import com.transaction.mini_transaction_tracker.core.domain.usecase.AddTransactionUseCase
import com.transaction.mini_transaction_tracker.core.domain.usecase.ValidateTransactionUseCase
import com.transaction.mini_transaction_tracker.core.domain.model.ValidationField
import com.transaction.mini_transaction_tracker.core.domain.model.ValidationResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDateTime

data class AddTransactionUiState(
    val amount: String = "",
    val description: String = "",
    val date: LocalDateTime = LocalDateTime.now(),
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
        _uiState.update { it.copy(amount = amount, amountError = null) }
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

    private fun validateForm(): BigDecimal? {
        val currentState = _uiState.value
        val parsedAmount = currentState.amount.toBigDecimalOrNull()

        if (parsedAmount == null) {
            _uiState.update {
                it.copy(
                    amountError = "Enter a valid number.",
                    descriptionError = if (currentState.description.isBlank())
                        "Description cannot be empty." else null,
                    dateError = null
                )
            }
            return null
        }

        val draftTransaction = Transaction(
            amount = parsedAmount,
            description = currentState.description,
            date = currentState.date,
            type = currentState.type
        )

        val result = validateTransactionUseCase(draftTransaction)

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

        return if (result is ValidationResult.Valid) parsedAmount else null
    }

    fun submitTransaction() {
        val parsedAmount = validateForm() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            try {
                val state = _uiState.value
                val transaction = Transaction(
                    amount = parsedAmount,
                    description = state.description,
                    date = state.date,
                    type = state.type
                )
                addTransactionUseCase(transaction)
                _eventChannel.send(AddTransactionViewEvent.Success)
            } catch (e: Exception) {
                _eventChannel.send(AddTransactionViewEvent.Error(e.message ?: "An unexpected error occurred"))
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }
}