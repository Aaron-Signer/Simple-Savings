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
        "select c.uid as uid, c.name, c.groupUid as groupUid, c.planned, c.spendingType, c.spent\n" +
                "from `groups` as g \n" +
                "join (select c.groupUid, c.uid, c.name, c.spendingType, c.planned, sum(t.credit) as spent\n" +
                "      from category as c\n" +
                "      join transactions as t\n" +
                "      on c.uid = t.categoryUid\n" +
                "      group by c.uid )as c \n" +
                "on g.uid = c.groupUid \n" +
                "where g.uid = :groupUid"
    )
    fun getCategoriesForGroup(groupUid: Int): Flow<List<Category>>
}