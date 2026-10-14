
package com.transaction.shared.di

import com.transaction.shared.data.local.DatabaseBuilderFactory
import com.transaction.shared.data.local.TransactionDatabase
import com.transaction.shared.data.local.createTransactionDatabase
import com.transaction.shared.data.repository.TransactionRepositoryImpl
import com.transaction.shared.domain.repository.TransactionRepository
import com.transaction.shared.domain.seed.SeedAssetReader
import com.transaction.shared.domain.seed.SeedDataSource
import org.koin.dsl.module

object DataBaseModule {
    val module = module {
        single<TransactionDatabase> { createTransactionDatabase(get<DatabaseBuilderFactory>()) }
        single { get<TransactionDatabase>().transactionDao() }
        single<TransactionRepository> { TransactionRepositoryImpl(dao = get()) }
        single { SeedDataSource(get<SeedAssetReader>()) }
    }
}