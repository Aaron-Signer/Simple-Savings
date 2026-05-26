package com.example.simplesavings.model.transaction

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.simplesavings.model.category.Category
import com.example.simplesavings.model.group.Group
import java.time.Instant

@Entity(tableName = "transactions")
data class Transaction (
    @PrimaryKey val uid: String,
    val categoryUid: Int, // FK to Group UID

    val dateTime: Instant = Instant.now(),

    var debit: Double = 0.0,
    var credit: Double = 0.0,

    var businessName: String? = "",

    @Ignore
    var categoryName: String? = ""
) {
    constructor(uid: String, categoryUid: Int, dateTime: Instant, debit: Double, credit: Double, businessName: String?) :
            this(uid, categoryUid, dateTime,debit, credit, businessName, categoryName = "")

}