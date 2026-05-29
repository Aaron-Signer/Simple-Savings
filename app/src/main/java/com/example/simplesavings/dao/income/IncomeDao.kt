package com.example.simplesavings.dao.income

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.simplesavings.model.category.Category
import com.example.simplesavings.model.group.Group
import com.example.simplesavings.model.income.Income
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Query("SELECT * FROM income where month = :month and year = :year")
    fun getAll(month: String, year: String): Flow<List<Income>>

    @Insert
    suspend fun insert(income: Income)

    @Delete
    suspend fun delete(income: Income)

//    @Query(
//        "SELECT * FROM groups " +
//                "JOIN category ON groups.uid = category.uid"
//    )
//    fun loadGroupsAndCategories(): Flow<Map<Group, List<Category>>>
}