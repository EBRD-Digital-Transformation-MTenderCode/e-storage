package com.procurement.storage.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

fun nowUTC() = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)

fun LocalDateTime.toDate(): Date {
    return Date.from(this.toInstant(ZoneOffset.UTC))
}

fun Date.toLocal(): LocalDateTime {
    return LocalDateTime.ofInstant(this.toInstant(), ZoneOffset.UTC)
}
