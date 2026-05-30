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
    @Query("select g.uid,\n" +
            "g.name,\n" +
            "sum(z.planned) as plannedTotal,\n" +
            " sum(z.credit) as spentTotal,\n" +
            " g.month,\n" +
            " g.year\n" +
            "from `groups` as g\n" +
            "join (select *\n" +
            "from category as c\n" +
            "join transactions as t\n" +
            "on c.uid = t.categoryUid) z\n" +
            "on g.uid = z.groupUid\n" +
            "group by g.uid\n" +
            "having month = :month and year = :year")
    fun getAll(month: String, year: String): Flow<List<Group>>

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