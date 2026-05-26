package com.example.simplesavings.model.transaction

import java.time.Instant

data class TransactionWithCategory(
    val uid: String,
    val categoryUid: Int,
    val dateTime: Instant,
    val debit: Double,
    val credit: Double,
    val businessName: String,
    val categoryName: String? // This comes from the 'category' table
)

// The Mapper Extension
fun TransactionWithCategory.toEntity(): Transaction {
    return Transaction(
        uid = this.uid,
        categoryUid = this.categoryUid,
        dateTime = this.dateTime,
        debit = this.debit,
        credit = this.credit,
        businessName = this.businessName
    ).apply {
        this.categoryName = this@toEntity.categoryName ?: "" // Map the ignored field here
    }
}