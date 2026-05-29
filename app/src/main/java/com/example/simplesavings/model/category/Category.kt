package com.example.simplesavings.model.category

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category (
    @PrimaryKey(autoGenerate = true)
    val uid: Int = -1,
    var groupUid: Int = -1, // FK to Group UID
    val name: String = "",
    val planned: Double = 0.0,
    var spent: Double = 0.0,
    val spendingType: SpendingType = SpendingType.FIXED
)