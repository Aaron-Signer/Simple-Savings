package com.example.simplesavings.config.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.simplesavings.dao.category.CategoryDao
import com.example.simplesavings.model.group.Group
import com.example.simplesavings.model.category.Category
import com.example.simplesavings.model.transaction.Transaction
import com.example.simplesavings.model.income.Income

import com.example.simplesavings.dao.group.GroupDao
import com.example.simplesavings.dao.income.IncomeDao
import com.example.simplesavings.dao.transaction.TransactionDao
import com.example.simplesavings.util.db.DateConverters


@Database(entities = [
        Group::class,
        Category::class,
        Transaction::class,
        Income::class],
    version = 21)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao

    abstract fun categoryDao(): CategoryDao

    abstract fun transactionDao(): TransactionDao

    abstract fun incomeDao(): IncomeDao
}