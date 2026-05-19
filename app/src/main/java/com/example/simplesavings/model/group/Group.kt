package com.example.simplesavings.model.group

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class Group (
    @PrimaryKey(autoGenerate = true) val uid: Int,
    val name: String
)