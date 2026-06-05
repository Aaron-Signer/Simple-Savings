package com.example.simplesavings.dao.transaction

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.simplesavings.model.transaction.Transaction
import com.example.simplesavings.model.transaction.TransactionSummary
import com.example.simplesavings.model.transaction.TransactionWithCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
abstract class TransactionDao {
    @Query("SELECT * FROM transactions")
    abstract fun getAll(): Flow<List<Transaction>>

    @Query("""
    SELECT totalDebit, day, year, month 
    FROM (
        SELECT 
            SUM(credit) as totalDebit,
            strftime('%d', dateTime / 1000, 'unixepoch') as day,
            strftime('%Y', dateTime / 1000, 'unixepoch') as year,
            CASE strftime('%m', dateTime / 1000, 'unixepoch')
                WHEN '01' THEN 'January'
                WHEN '02' THEN 'February'
                WHEN '03' THEN 'March'
                WHEN '04' THEN 'April'
                WHEN '05' THEN 'May'
                WHEN '06' THEN 'June'
                WHEN '07' THEN 'July'
                WHEN '08' THEN 'August'
                WHEN '09' THEN 'September'
                WHEN '10' THEN 'October'
                WHEN '11' THEN 'November'
                WHEN '12' THEN 'December'
            END AS month
        FROM transactions
        GROUP BY year, month, day
    )
    WHERE month = :month AND year = :year
""")
    abstract fun getTransactionSummaryForMonth(year: String, month: String): Flow<List<TransactionSummary>>

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
                "left join category as c on c.uid = t.categoryUid order by t.dateTime desc"
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
