package com.procurement.storage.lib

inline fun <T : String?, E : RuntimeException> T.takeIfNotEmpty(error: () -> E): T =
    if (this != null && this.isBlank()) throw error() else this

fun <T> T?.takeIfNotNullOrDefault(default: T?): T? = this ?: default
