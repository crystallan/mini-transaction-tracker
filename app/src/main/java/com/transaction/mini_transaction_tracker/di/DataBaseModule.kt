package com.transaction.mini_transaction_tracker.di

import androidx.room.Room
import com.transaction.mini_transaction_tracker.core.data.local.TransactionDatabase
import com.transaction.mini_transaction_tracker.core.data.repository.TransactionRepositoryImpl
import com.transaction.mini_transaction_tracker.core.domain.repository.TransactionRepository
import com.transaction.mini_transaction_tracker.core.seed.SeedDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object DataBaseModule {
    val module = module {
        single {
            Room.databaseBuilder(androidContext(),
                TransactionDatabase::class.java,
                "transaction_database").build()
        }
        single { get<TransactionDatabase>().transactionDao() }
        single<TransactionRepository> { TransactionRepositoryImpl(dao = get()) }
        single { SeedDataSource(androidContext()) }
    }
}