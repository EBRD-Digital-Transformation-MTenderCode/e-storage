package com.procurement.storage.exception

enum class ErrorType constructor(val code: String, val message: String) {
    DATA_NOT_FOUND("00.01", "File not found by id"),
    INVALID_ID("00.02", "Invalid documents id"),
    CONTEXT("20.01", "Context parameter not found.");
}
