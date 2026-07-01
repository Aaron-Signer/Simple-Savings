package com.example.simplesavings.config.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 25,
    exportSchema = true)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao

    abstract fun categoryDao(): CategoryDao

    abstract fun transactionDao(): TransactionDao

    abstract fun incomeDao(): IncomeDao
}

val MIGRATION_24_25 = object : Migration(24, 25) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add groupOrder column to groups table
        db.execSQL("ALTER TABLE `groups` ADD COLUMN `groupOrder` INTEGER NOT NULL DEFAULT 0")

        // Create income table if it doesn't exist
        db.execSQL("CREATE TABLE IF NOT EXISTS `income` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `amount` REAL NOT NULL, `month` TEXT NOT NULL, `year` TEXT NOT NULL)")
    }
}