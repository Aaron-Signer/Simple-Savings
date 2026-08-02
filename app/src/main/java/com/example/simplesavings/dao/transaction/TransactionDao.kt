package com.example.simplesavings.dao.transaction

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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
    WITH RECURSIVE
      -- 1. Generate a list of numbers from 1 to 31
      all_days(day_num) AS (
        SELECT 1
        UNION ALL
        SELECT day_num + 1 FROM all_days WHERE day_num < 31
      ),
      -- 2. Calculate totals for days that actually have transactions
      daily_totals AS (
        SELECT 
            SUM(credit) as total,
            CAST(strftime('%d', dateTime / 1000, 'unixepoch') AS INTEGER) as d,
            strftime('%Y', dateTime / 1000, 'unixepoch') as y,
            CASE strftime('%m', dateTime / 1000, 'unixepoch')
                WHEN '01' THEN 'January' WHEN '02' THEN 'February' WHEN '03' THEN 'March'
                WHEN '04' THEN 'April' WHEN '05' THEN 'May' WHEN '06' THEN 'June'
                WHEN '07' THEN 'July' WHEN '08' THEN 'August' WHEN '09' THEN 'September'
                WHEN '10' THEN 'October' WHEN '11' THEN 'November' WHEN '12' THEN 'December'
            END AS m
        FROM transactions
        WHERE y = :year AND m = :month
        GROUP BY d
      )
    -- 3. Join the generated days with the actual totals
    SELECT 
        COALESCE(dt.total, 0.0) as totalDebit,
        printf('%02d', ad.day_num) as day, 
        :year as year,
        :month as month
    FROM all_days ad
    LEFT JOIN daily_totals dt ON ad.day_num = dt.d
    -- 4. This WHERE clause removes days that don't exist (like June 31 or Feb 30)
    WHERE date(:year || '-' || 
          CASE :month 
            WHEN 'January' THEN '01' WHEN 'February' THEN '02' WHEN 'March' THEN '03'
            WHEN 'April' THEN '04' WHEN 'May' THEN '05' WHEN 'June' THEN '06'
            WHEN 'July' THEN '07' WHEN 'August' THEN '08' WHEN 'September' THEN '09'
            WHEN 'October' THEN '10' WHEN 'November' THEN '11' WHEN 'December' THEN '12'
          END || '-' || printf('%02d', ad.day_num)) IS NOT NULL
    ORDER BY ad.day_num ASC
""")
    abstract fun getTransactionSummaryForMonth(year: String, month: String): Flow<List<TransactionSummary>>

    @Insert
    abstract suspend fun insert(transaction: Transaction)

    @Delete
    abstract suspend fun delete(transaction: Transaction)

    @Update
    abstract suspend fun update(transaction: Transaction)

    @Query("""
        SELECT t.uid, t.categoryUid, t.dateTime, t.debit, t.credit, t.businessName
        FROM category AS c
        JOIN transactions AS t ON c.uid = t.categoryUid
        WHERE c.uid = :categoryUid
    """)
    abstract fun getTransactionsForCategory(categoryUid: Int): Flow<List<Transaction>>

    @Query("""
        SELECT
            COALESCE(c.name, '') AS categoryName,
            t.uid,
            t.categoryUid,
            t.dateTime,
            t.debit,
            t.credit,
            COALESCE(t.businessName, '') AS businessName
        FROM (SELECT * FROM transactions
                where strftime('%Y', dateTime / 1000, 'unixepoch') = :year
                  and CASE strftime('%m', dateTime / 1000, 'unixepoch')
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
                        END = :month) AS t
        LEFT JOIN category AS c ON c.uid = t.categoryUid
        ORDER BY t.dateTime DESC
    """)
    protected abstract fun getTransactionsInternal(year: String, month: String): Flow<List<TransactionWithCategory>>

    // 2. The "Public" function you actually call
    fun getTransactionsWithNames(year: String, month: String): Flow<List<Transaction>> {
        return getTransactionsInternal(year, month).map { list ->
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
