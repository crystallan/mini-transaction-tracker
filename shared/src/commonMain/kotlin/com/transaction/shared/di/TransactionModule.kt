package com.transaction.shared.di

import com.transaction.shared.presentation.transaction.add.presentation.AddTransactionViewModel
import com.transaction.shared.presentation.transaction.detail.presentation.TransactionDetailViewModel
import com.transaction.shared.presentation.transaction.view.presentation.ViewTransactionViewModel
import com.transaction.shared.domain.usecases.AddTransactionUseCase
import com.transaction.shared.domain.usecases.DeleteTransactionUseCase
import com.transaction.shared.domain.usecases.EditTransactionUseCase
import com.transaction.shared.domain.usecases.GetBalanceUseCase
import com.transaction.shared.domain.usecases.GetTransactionDetailUseCase
import com.transaction.shared.domain.usecases.GetTransactionUseCase
import com.transaction.shared.domain.usecases.SeedDatabaseUseCase
import com.transaction.shared.domain.usecases.ValidateTransactionUseCase
import com.transaction.shared.presentation.transaction.edit.presentation.EditTransactionViewModel
import com.transaction.shared.storage.SeedStateStore
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

object TransactionModule {
    val module = module {
        factory { ValidateTransactionUseCase() }
        factory { AddTransactionUseCase(get(), get()) }
        factory { EditTransactionUseCase(get(), get()) }
        factory { GetTransactionUseCase(get()) }
        factory { GetTransactionDetailUseCase(get()) }
        factory { GetBalanceUseCase(get()) }
        factory { DeleteTransactionUseCase(get()) }
        factory { SeedDatabaseUseCase(get(), get(), get(), get()) }
        single { SeedStateStore(get()) }
        viewModel { ViewTransactionViewModel(get(), get(), get()) }
        viewModel { AddTransactionViewModel(get(), get()) }
        factory { (id : Int) -> EditTransactionViewModel(get(), get(), get(),get()) }
        factory { (id: Int) -> TransactionDetailViewModel( id , get()) }
    }
}