package com.example.simplesavings.config.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.simplesavings.dao.category.CategoryDao
import com.example.simplesavings.model.group.Group
import com.example.simplesavings.model.category.Category
import com.example.simplesavings.model.transaction.Transaction
import com.example.simplesavings.dao.group.GroupDao
import com.example.simplesavings.dao.transaction.TransactionDao


@Database(entities = [Group::class, Category::class, Transaction::class], version = 10)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao

    abstract fun categoryDao(): CategoryDao

    abstract fun transactionDao(): TransactionDao
}