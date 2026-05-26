package com.example.simplesavings.dao.transaction

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.simplesavings.model.transaction.Transaction
import com.example.simplesavings.model.transaction.TransactionWithCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
abstract class TransactionDao {
    @Query("SELECT * FROM transactions")
    abstract fun getAll(): Flow<List<Transaction>>

    @Insert
    abstract suspend fun insert(transaction: Transaction)

    @Delete
    abstract suspend fun delete(transaction: Transaction)

    @Query(
        "select t.uid, t.categoryUid, t.dateTime, t.debit, t.credit, t.businessName from category as c " +
                "join transactions as t on c.uid = t.categoryUid " +
                "where c.uid = :categoryUid"
    )
    abstract fun getTransactionsForCategory(categoryUid: Int): Flow<List<Transaction>>

    @Query(
        "select COALESCE(c.name, '') as categoryName, t.uid, t.categoryUid, t.dateTime, t.debit, t.credit, COALESCE(t.businessName, '') as businessName from transactions as t " +
                "left join category as c on c.uid = t.categoryUid"
    )
    protected abstract fun getTransactionsInternal(): Flow<List<TransactionWithCategory>>

    // 2. The "Public" function you actually call
    fun getTransactionsWithNames(): Flow<List<Transaction>> {
        return getTransactionsInternal().map { list ->
            list.map { result ->
                Transaction(
                    uid = result.uid,
                    categoryUid = result.categoryUid,
                    dateTime = result.dateTime,
                    debit = result.debit,
                    credit = result.credit,
                    businessName = result.businessName 
                ).apply {
                    this.categoryName = result.categoryName ?: "" // Populate the @Ignore field
                }
            }
        }
    }
}
