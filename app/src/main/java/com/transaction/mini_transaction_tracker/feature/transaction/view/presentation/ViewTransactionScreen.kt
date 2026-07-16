package com.transaction.mini_transaction_tracker.feature.transaction.view.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
    var sortMenuExpanded by remember { mutableStateOf(false) }

    fun displayAmount(amount: BigDecimal): String =
        if (displayCurrency == Currency.KES) CurrencyUtils.format(amount, Currency.KES)
        else CurrencyUtils.convertAndFormat(amount, Currency.KES, Currency.USD)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions") },
                actions = {
                    IconButton(onClick = viewModel::onDisplayCurrencyToggled) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Toggle currency (${displayCurrency.code})")
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
                        text = displayAmount(balance),
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (balance < BigDecimal.ZERO) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
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
                                        formattedAmount = displayAmount(transaction.amount),
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
        text = { Text("Are you sure you want to delete this transaction?") },
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = DateUtils.formatDateOnly(transaction.date),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = transaction.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formattedAmount,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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