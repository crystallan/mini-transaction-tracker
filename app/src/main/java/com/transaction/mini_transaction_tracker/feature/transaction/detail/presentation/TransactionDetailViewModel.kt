package com.transaction.mini_transaction_tracker.feature.transaction.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transaction.mini_transaction_tracker.core.domain.model.Transaction
import com.transaction.mini_transaction_tracker.core.domain.usecase.GetTransactionDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DetailTransactionUiState {
    data object Loading : DetailTransactionUiState
    data class Success(val transaction: Transaction) : DetailTransactionUiState
    data class Error(val message: String) : DetailTransactionUiState
}

class TransactionDetailViewModel(
    private val transactionId: Int,
    private val getTransactionDetailUseCase: GetTransactionDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailTransactionUiState>(DetailTransactionUiState.Loading)
    val uiState: StateFlow<DetailTransactionUiState> = _uiState.asStateFlow()

    init {
        loadTransactionDetail()
    }

    fun loadTransactionDetail() {
        viewModelScope.launch {
            _uiState.value = DetailTransactionUiState.Loading
            try {
                val transaction = getTransactionDetailUseCase(transactionId)
                _uiState.value = if (transaction != null) {
                    DetailTransactionUiState.Success(transaction)
                } else {
                    DetailTransactionUiState.Error("Transaction not found.")
                }
            } catch (e: Exception) {
                _uiState.value = DetailTransactionUiState.Error(e.message ?: "Failed to load transaction.")
            }
        }
    }
}