package com.example.simplesavings.dao.category

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.simplesavings.model.category.Category
import com.example.simplesavings.model.group.Group
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAll(): Flow<List<Category>>

    @Insert
    suspend fun insert(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query(
        "select c.uid as uid, c.name, c.groupUid as groupUid, c.planned, c.spendingType, c.spent " +
                "from `groups` as g " +
                "join (select c.groupUid, c.uid, c.name, c.spendingType, c.planned, TOTAL(t.credit) as spent " +
                "      from category as c " +
                "      left join transactions as t " +
                "      on c.uid = t.categoryUid " +
                "      group by c.uid ) as c " +
                "on g.uid = c.groupUid " +
                "where g.uid = :groupUid"
    )
    fun getCategoriesForGroup(groupUid: Int): Flow<List<Category>>
}