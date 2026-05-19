package com.example.simplesavings.config.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.simplesavings.model.group.Group
import com.example.simplesavings.dao.group.GroupDao


@Database(entities = [Group::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
}