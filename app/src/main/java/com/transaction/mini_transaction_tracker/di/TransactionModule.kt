package com.transaction.mini_transaction_tracker.di

import com.transaction.mini_transaction_tracker.core.domain.usecase.AddTransactionUseCase
import com.transaction.mini_transaction_tracker.core.domain.usecase.DeleteTransactionUseCase
import com.transaction.mini_transaction_tracker.core.domain.usecase.GetBalanceUseCase
import com.transaction.mini_transaction_tracker.core.domain.usecase.GetTransactionDetailUseCase
import com.transaction.mini_transaction_tracker.core.domain.usecase.GetTransactionUseCase
import com.transaction.mini_transaction_tracker.core.domain.usecase.SeedDatabaseUseCase
import com.transaction.mini_transaction_tracker.core.domain.usecase.ValidateTransactionUseCase
import com.transaction.mini_transaction_tracker.feature.transaction.add.presentation.AddTransactionViewModel
import com.transaction.mini_transaction_tracker.feature.transaction.detail.presentation.TransactionDetailViewModel
import com.transaction.mini_transaction_tracker.feature.transaction.view.presentation.ViewTransactionViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

object TransactionModule {
    val module = module {
        factory { ValidateTransactionUseCase() }
        factory { AddTransactionUseCase(get(), get()) }
        factory { GetTransactionUseCase(get()) }
        factory { GetTransactionDetailUseCase(get()) }
        factory { GetBalanceUseCase(get()) }
        factory { DeleteTransactionUseCase(get()) }
        factory { SeedDatabaseUseCase(get(), get()) }

        viewModel { ViewTransactionViewModel(get(), get()) }
        viewModel { AddTransactionViewModel() }
        viewModel { TransactionDetailViewModel() }
    }
}