package com.transaction.mini_transaction_tracker.feature.transaction.view.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.transaction.mini_transaction_tracker.core.domain.model.Transaction
import com.transaction.mini_transaction_tracker.core.domain.model.TransactionType
import com.transaction.mini_transaction_tracker.core.domain.utils.OrderType
import com.transaction.mini_transaction_tracker.core.domain.utils.TransactionOrder
import com.transaction.mini_transaction_tracker.core.utils.Currency
import com.transaction.mini_transaction_tracker.core.utils.CurrencyUtils
import com.transaction.mini_transaction_tracker.core.utils.DateUtils
import org.koin.androidx.compose.koinViewModel
import java.math.BigDecimal
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewTransactionScreen(
    onTransactionClick: (Int) -> Unit,
    onNewTransactionClick: () -> Unit,
    viewModel: ViewTransactionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pendingDelete by viewModel.pendingDeleteTransaction.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    val currentOrder by viewModel.currentOrder.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val balanceError by viewModel.balanceError.collectAsState()
    val displayCurrency by viewModel.displayCurrency.collectAsState()
    var showDateFilterSheet by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var endDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    fun displayAmount(amount: BigDecimal, type: TransactionType): String {
        val formatted = if (displayCurrency == Currency.KES) CurrencyUtils.format(amount, Currency.KES)
        else CurrencyUtils.convertAndFormat(amount, Currency.KES, Currency.USD)
        val sign = if (type == TransactionType.CREDIT) "+ " else "- "
        return sign + formatted
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions") },
                actions = {
                    IconButton(onClick = viewModel::onDisplayCurrencyToggled) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Toggle currency (${displayCurrency.code})")
                    }
                    IconButton(onClick = { showDateFilterSheet = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Filter by date")
                    }
                    Box {
                        IconButton(onClick = { sortMenuExpanded = true }) {
                            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                        }
                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Date (newest)") },
                                trailingIcon = {
                                    if (currentOrder is TransactionOrder.Date && currentOrder.orderType == OrderType.Descending) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                },
                                onClick = {
                                    viewModel.onSortOrderSelected(TransactionOrder.Date(OrderType.Descending)); sortMenuExpanded =
                                    false
                                })
                            DropdownMenuItem(
                                text = { Text("Date (oldest)") },
                                trailingIcon = {
                                    if (currentOrder is TransactionOrder.Date && currentOrder.orderType == OrderType.Ascending) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                },
                                onClick = {
                                    viewModel.onSortOrderSelected(TransactionOrder.Date(OrderType.Ascending)); sortMenuExpanded =
                                    false
                                })
                            DropdownMenuItem(
                                text = { Text("Amount (high to low)") },
                                trailingIcon = {
                                    if (currentOrder is TransactionOrder.Amount && currentOrder.orderType == OrderType.Descending) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                },
                                onClick = {
                                    viewModel.onSortOrderSelected(TransactionOrder.Amount(OrderType.Descending)); sortMenuExpanded =
                                    false
                                })
                            DropdownMenuItem(
                                text = { Text("Amount (low to high)") },
                                trailingIcon = {
                                    if (currentOrder is TransactionOrder.Amount && currentOrder.orderType == OrderType.Ascending) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                },
                                onClick = {
                                    viewModel.onSortOrderSelected(TransactionOrder.Amount(OrderType.Ascending)); sortMenuExpanded =
                                    false
                                })
                            DropdownMenuItem(
                                text = { Text("Description (A-Z)") },
                                trailingIcon = {
                                    if (currentOrder is TransactionOrder.Description && currentOrder.orderType == OrderType.Ascending) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                },
                                onClick = {
                                    viewModel.onSortOrderSelected(
                                        TransactionOrder.Description(OrderType.Ascending)
                                    ); sortMenuExpanded = false
                                })
                            DropdownMenuItem(
                                text = { Text("Description (Z-A)") },
                                trailingIcon = {
                                    if (currentOrder is TransactionOrder.Description && currentOrder.orderType == OrderType.Descending) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                },
                                onClick = {
                                    viewModel.onSortOrderSelected(
                                        TransactionOrder.Description(OrderType.Descending)
                                    ); sortMenuExpanded = false
                                })
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewTransactionClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            OutlinedTextField(
                value = currentFilter.keyword,
                onValueChange = viewModel::onKeywordChanged,
                placeholder = { Text("Search description...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Balance", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = if (displayCurrency == Currency.KES) CurrencyUtils.format(balance, Currency.KES)
                        else CurrencyUtils.convertAndFormat(balance, Currency.KES, Currency.USD),
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (balance < BigDecimal.ZERO) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                    if (balance < BigDecimal.ZERO) {
                        Text(
                            text = "Your balance is negative.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    if (balanceError != null) {
                        Text(
                            text = balanceError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = currentFilter.type == null,
                    onClick = { viewModel.onTypeFilterSelected(null) },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = currentFilter.type == TransactionType.CREDIT,
                    onClick = { viewModel.onTypeFilterSelected(TransactionType.CREDIT) },
                    label = { Text("Credit") }
                )
                FilterChip(
                    selected = currentFilter.type == TransactionType.DEBIT,
                    onClick = { viewModel.onTypeFilterSelected(TransactionType.DEBIT) },
                    label = { Text("Debit") }
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is ViewTransactionUiState.Loading ->
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is ViewTransactionUiState.Error ->
                        Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                    is ViewTransactionUiState.Success -> {
                        if (state.transactions.isEmpty()) {
                            Text("No Transactions", modifier = Modifier.align(Alignment.Center))
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.transactions) { transaction ->
                                    TransactionItem(
                                        transaction = transaction,
                                        formattedAmount = displayAmount(transaction.amount, transaction.type),
                                        onClick = { onTransactionClick(transaction.id) },
                                        onDeleteClick = { viewModel.requestDelete(transaction) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { transaction ->
        DeleteConfirmationDialog(
            transaction = transaction,
            onConfirm = viewModel::confirmDelete,
            onDismiss = viewModel::dismissDeleteConfirmation
        )
    }

    if (showDateFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showDateFilterSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text("Filter by date", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(startDate?.let { DateUtils.formatDateOnly(it) } ?: "Start date")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { showEndDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(endDate?.let { DateUtils.formatDateOnly(it) } ?: "End date")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = {
                        startDate = null
                        endDate = null
                        viewModel.onDateRangeSelected(null, null)
                        showDateFilterSheet = false
                    }) { Text("Clear") }

                    Button(onClick = {
                        viewModel.onDateRangeSelected(startDate, endDate)
                        showDateFilterSheet = false
                    }) { Text("Apply") }
                }
            }
        }
    }

    if (showStartDatePicker) {
        val startPickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startPickerState.selectedDateMillis?.let { millis ->
                        startDate = DateUtils.startOfDay(DateUtils.millisToLocalDate(millis))
                    }
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = startPickerState)
        }
    }

    if (showEndDatePicker) {
        val endPickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endPickerState.selectedDateMillis?.let { millis ->
                        endDate = DateUtils.endOfDay(DateUtils.millisToLocalDate(millis))
                    }
                    showEndDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = endPickerState)
        }
    }

}

@Composable
private fun DeleteConfirmationDialog(
    transaction: Transaction,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Transaction") },
        text = { Text("Delete \"${transaction.description}\"? This can't be undone.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    formattedAmount: String,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = DateUtils.formatDateOnly(transaction.date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formattedAmount,
                    style = MaterialTheme.typography.titleMedium,
                    color = when (transaction.type) {
                        TransactionType.CREDIT -> MaterialTheme.colorScheme.primary
                        TransactionType.DEBIT -> MaterialTheme.colorScheme.error
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Transaction"
                    )
                }
            }
        }
    }
}