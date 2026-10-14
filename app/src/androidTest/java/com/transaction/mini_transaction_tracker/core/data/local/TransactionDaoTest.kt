package com.transaction.mini_transaction_tracker.core.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.transaction.shared.data.local.TransactionDao
import com.transaction.shared.data.local.TransactionDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionDaoTest {

    private lateinit var db: TransactionDatabase
    private lateinit var dao: TransactionDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TransactionDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = db.transactionDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun getAllTransactions_whenTableIsEmpty_emitsEmptyList() = runTest {
        dao.getAllTransactions().test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
        }
    }

    @ExtensionFunctionType
}