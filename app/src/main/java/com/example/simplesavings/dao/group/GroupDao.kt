package com.example.simplesavings.dao.group

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.simplesavings.model.category.Category
import com.example.simplesavings.model.group.Group
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Query("""
        WITH GroupPlanned AS (
            -- Sum planned values once per category, grouped by groupUid
            SELECT groupUid, SUM(planned) as totalPlanned
            FROM category
            GROUP BY groupUid
        )
        SELECT 
            g.uid, 
            g.name, 
            COALESCE(gp.totalPlanned, 0) as plannedTotal, 
            SUM(t.credit) as spentTotal,
            g.month, 
            g.year,
            g.groupOrder
        FROM `groups` g
        LEFT JOIN GroupPlanned gp ON g.uid = gp.groupUid
        LEFT JOIN category c ON g.uid = c.groupUid
        LEFT JOIN transactions t ON c.uid = t.categoryUid
        WHERE g.month = :month AND g.year = :year
        GROUP BY g.uid
        ORDER BY g.groupOrder ASC
    """)
    fun getAll(month: String, year: String): Flow<List<Group>>

    @androidx.room.Update
    suspend fun update(group: Group)

    @androidx.room.Update
    suspend fun updateAll(groups: List<Group>)

    @Query("""
        SELECT SUM(
            CASE
                WHEN spendingType = 'FIXED' THEN planned
                WHEN spendingType = 'VARIABLE' THEN catSpendTotal
                ELSE 0
            END
        ) AS total_sum
        FROM (
            SELECT 
                c.*,
                SUM(t.credit) as catSpendTotal
            FROM category AS c
            LEFT JOIN transactions AS t ON c.uid = t.categoryUid
            GROUP BY c.uid
            HAVING categoryMonth = :month AND categoryYear = :year
        )
    """)
    fun getBudgetSummary(month: String, year: String): Flow<Double>

    @Query("""
        SELECT SUM(
            CASE
                WHEN spendingType = 'FIXED' THEN planned
                WHEN spendingType = 'VARIABLE' THEN 
                     CASE
                          WHEN catSpendTotal > planned THEN catSpendTotal
                          ELSE planned
                    END
                WHEN spendingType = 'RECURRING' THEN 
                     CASE
                          WHEN catSpendTotal > 0 THEN catSpendTotal
                          ELSE planned
                    END
                ELSE 0
            END
        ) AS total_sum
        FROM (
            SELECT 
                c.*,
                SUM(t.credit) as catSpendTotal
            FROM category AS c
            LEFT JOIN transactions AS t ON c.uid = t.categoryUid
            GROUP BY c.uid
            HAVING categoryMonth = :month AND categoryYear = :year
        )
    """)
    fun getPlannedProjectedBudget(month: String, year: String): Flow<Double>

    @Query("""
        select sum (c.planned)
          from category c
         where categoryMonth = :month AND categoryYear = :year
    """)
    fun getPlannedBudget(month: String, year: String): Flow<Double>

    @Insert
    suspend fun insert(group: Group): Long

    @Delete
    suspend fun delete(group: Group)

    @Query(
        "SELECT * FROM groups " +
                "JOIN category ON groups.uid = category.uid"
    )
    fun loadGroupsAndCategories(): Flow<Map<Group, List<Category>>>
}