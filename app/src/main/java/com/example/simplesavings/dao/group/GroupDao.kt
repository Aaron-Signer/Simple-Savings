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
    @Query("select g.uid, " +
            "               g.name, " +
            "               sum(z.planned) as plannedTotal, " +
            "               sum(z.credit) as spentTotal, " +
            "               g.month, " +
            "               g.year " +
            "       from `groups` as g " +
            "       left join (select *  " +
            "               from category as c " +
            "          left join transactions as t " +
            "               on c.uid = t.categoryUid) z " +
            "         on g.uid = z.groupUid " +
            "   group by g.uid " +
            "     having month = :month and year = :year")
    fun getAll(month: String, year: String): Flow<List<Group>>

    @Query("SELECT SUM( \n" +
            "CASE\n" +
            "WHEN  spendingType = 'FIXED'\n" +
            "THEN planned \n" +
            "WHEN spendingType = 'VARIABLE'\n" +
            " THEN catSpendTotal \n" +
            " ELSE \n" +
            " 0 \n" +
            " END ) AS total_sum\n" +
            "from \n" +
            "    (SELECT *,\n" +
            "                      c.name as catName,\n" +
            "                      sum(t.credit) as catSpendTotal,\n" +
            "                      c.categoryMonth\n" +
            "    FROM category AS c \n" +
            "    left JOIN transactions AS t \n" +
            "    ON c.uid = t.categoryUid\n" +
            "    group by c.uid\n" +
            "HAVING categoryMonth = :month AND categoryYear = :year)")
    fun getBudgetSummary(month: String, year: String): Flow<Double>

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