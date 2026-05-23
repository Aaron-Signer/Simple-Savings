package com.example.simplesavings.model.category

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category (
    @PrimaryKey(autoGenerate = true) val uid: Int,
    val groupUid: Int, // FK to Group UID
    val name: String
)