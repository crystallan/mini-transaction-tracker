package com.transaction.mini_transaction_tracker.feature.transaction.view.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transaction.mini_transaction_tracker.core.domain.model.Transaction
import com.transaction.mini_transaction_tracker.core.domain.model.TransactionType
import com.transaction.mini_transaction_tracker.core.domain.usecase.DeleteTransactionUseCase
import com.transaction.mini_transaction_tracker.core.domain.usecase.GetBalanceUseCase
import com.transaction.mini_transaction_tracker.core.domain.usecase.GetTransactionUseCase
import com.transaction.mini_transaction_tracker.core.domain.utils.OrderType
import com.transaction.mini_transaction_tracker.core.domain.utils.TransactionFilter
import com.transaction.mini_transaction_tracker.core.domain.utils.TransactionOrder
import com.transaction.mini_transaction_tracker.core.utils.Currency
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.math.BigDecimal

sealed interface ViewTransactionUiState{
    data object Loading : ViewTransactionUiState
    data class Success(val transactions: List<Transaction>) : ViewTransactionUiState
    data class Error (val message: String) : ViewTransactionUiState
}

class ViewTransactionViewModel(
    private val getTransactionUseCase: GetTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getBalanceUseCase: GetBalanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ViewTransactionUiState>(ViewTransactionUiState.Loading)
    val uiState: StateFlow<ViewTransactionUiState> = _uiState.asStateFlow()

    private val _pendingDeleteTransaction = MutableStateFlow<Transaction?>(null)
    val pendingDeleteTransaction: StateFlow<Transaction?> = _pendingDeleteTransaction.asStateFlow()

    private val _currentOrder = MutableStateFlow<TransactionOrder>(TransactionOrder.Date(OrderType.Descending))
    val currentOrder: StateFlow<TransactionOrder> = _currentOrder.asStateFlow()

    private val _currentFilter = MutableStateFlow(TransactionFilter())
    val currentFilter : StateFlow<TransactionFilter> = _currentFilter.asStateFlow()

    private val _balance = MutableStateFlow(BigDecimal.ZERO)
    val balance : StateFlow<BigDecimal> = _balance.asStateFlow()

    private val _balanceError = MutableStateFlow<String?>(null)
    val balanceError: StateFlow<String?> = _balanceError.asStateFlow()

    private val _displayCurrency = MutableStateFlow(Currency.KES)
    val displayCurrency: StateFlow<Currency> = _displayCurrency.asStateFlow()

    private var getTransactionsJob: Job? = null
    private var getBalanceJob: Job? = null

    init {
        loadTransactions()
        loadBalance()
    }

    fun loadTransactions() {
        getTransactionsJob?.cancel()
        getTransactionsJob = getTransactionUseCase(_currentOrder.value, _currentFilter.value)
                .onEach { transactions ->
                    _uiState.value = ViewTransactionUiState.Success(transactions)
                }
                .catch { exception ->
                    _uiState.value = ViewTransactionUiState.Error(
                        exception.message ?: "Failed to load Transactions")
                }
            .launchIn(viewModelScope)
        }

    fun onSortOrderSelected ( order : TransactionOrder){
        _currentOrder.value = order
        loadTransactions()
    }

    fun loadBalance(){
        getBalanceJob?.cancel()
        getBalanceJob = getBalanceUseCase()
            .onEach { total ->
                _balance.value = total
            }
            .catch { exception ->
                _balanceError.value = exception.message ?: "Failed to load balance"
            }
            .launchIn(viewModelScope)
    }

    fun onKeywordChanged (keyword : String){
        _currentFilter.value = _currentFilter.value.copy(keyword = keyword)
        loadTransactions()
    }

    fun onTypeFilterSelected(type : TransactionType?){
        _currentFilter.value = _currentFilter.value.copy(type = type)
        loadTransactions()
    }

    fun onDisplayCurrencyToggled() {
        _displayCurrency.value = if (_displayCurrency.value == Currency.KES) Currency.USD else Currency.KES
    }

    fun requestDelete(transaction: Transaction){
        _pendingDeleteTransaction.value = transaction
    }

    fun confirmDelete(){
        val transaction = _pendingDeleteTransaction.value ?: return
        viewModelScope.launch {
            deleteTransactionUseCase(transaction)
            _pendingDeleteTransaction.value = null
        }
    }

    fun dismissDeleteConfirmation(){
        _pendingDeleteTransaction.value = null
    }

}