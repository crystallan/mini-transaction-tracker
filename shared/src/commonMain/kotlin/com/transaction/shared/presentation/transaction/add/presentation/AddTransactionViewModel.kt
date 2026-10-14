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

    fun validateForm(): Decimal? {
        val currentState = _uiState.value
        val result = validateTransactionUseCase(
            currentState.amount,
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

        return currentState.amount.toDecimalOrNull()
            ?.setScale(2, RoundingMode.HALF_UP)
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