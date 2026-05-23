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
    @Query("SELECT * FROM groups")
    fun getAll(): Flow<List<Group>>

    @Insert
    suspend fun insert(group: Group)

    @Delete
    suspend fun delete(group: Group)

    @Query(
        "SELECT * FROM groups " +
                "JOIN category ON groups.uid = category.uid"
    )
    fun loadGroupsAndCategories(): Flow<Map<Group, List<Category>>>
}