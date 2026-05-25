package com.example.simplesavings.dao.transaction

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.simplesavings.model.transaction.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions")
    fun getAll(): Flow<List<Transaction>>

    @Insert
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query(
        "select t.uid, t.categoryUid, t.debit, t.credit, t.businessName from category as c " +
                "join transactions as t on c.uid = t.categoryUid " +
                "where c.uid = :categoryUid"
    )
    fun getTransactionsForCategory(categoryUid: Int): Flow<List<Transaction>>
}