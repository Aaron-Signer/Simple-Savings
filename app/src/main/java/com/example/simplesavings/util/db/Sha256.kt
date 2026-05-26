package com.example.simplesavings.util.db

import java.security.MessageDigest
import java.time.Instant

fun getTransactionSha256Uid(businessName: String, credit: Double, dateTime: Instant): String {
    val tempUid = businessName + credit + dateTime

    val sha256HashUid = tempUid.sha256()

    return sha256HashUid
}

fun String.sha256(): String {
    return MessageDigest
        .getInstance("SHA-256")
        .digest(this.toByteArray())
        .fold("") { str, it -> str + "%02x".format(it) }
}