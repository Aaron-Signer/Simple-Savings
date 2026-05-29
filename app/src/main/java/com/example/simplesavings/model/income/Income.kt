package com.example.simplesavings.model.income

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income")
data class Income (
    @PrimaryKey(autoGenerate = true) val uid: Int,
    val name: String,

    var amount: Double = 0.0,

    var month: String = "",
    var year: String = ""
)