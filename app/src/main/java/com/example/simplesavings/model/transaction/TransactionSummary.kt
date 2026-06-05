package com.example.simplesavings.model.transaction

data class TransactionSummary(
    val month: String,
    val year: String,
    val day: String,
    val totalDebit: Double
)