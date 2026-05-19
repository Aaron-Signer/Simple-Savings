package com.example.simplesavings.config.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.simplesavings.dao.category.CategoryDao
import com.example.simplesavings.model.group.Group
import com.example.simplesavings.model.category.Category
import com.example.simplesavings.dao.group.GroupDao


@Database(entities = [Group::class, Category::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao

    abstract fun categoryDao(): CategoryDao
}