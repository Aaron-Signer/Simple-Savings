package com.example.simplesavings.dao.group

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.simplesavings.model.group.Group
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups")
    fun getAll(): Flow<List<Group>>

    @Insert
    suspend fun insert(group: Group)
}