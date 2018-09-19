package com.procurement.notice.exception

enum class ErrorType constructor(val code: String, val message: String) {
    DATA_NOT_FOUND("00.01", "File not found by id"),
    CONTEXT("20.01", "Context parameter not found.");
}
