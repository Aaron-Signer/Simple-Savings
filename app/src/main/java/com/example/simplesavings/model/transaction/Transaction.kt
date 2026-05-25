package com.example.simplesavings.model.transaction

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.simplesavings.model.category.Category
import com.example.simplesavings.model.group.Group

@Entity(tableName = "transactions")
data class Transaction (
    @PrimaryKey(autoGenerate = true) val uid: Int,
    val categoryUid: Int, // FK to Group UID

    var debit: Double = 0.0,
    var credit: Double = 0.0,

    var businessName: String = ""
)