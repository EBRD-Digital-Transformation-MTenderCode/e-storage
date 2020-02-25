package com.procurement.storage.utils

sealed class ValidationResult<out T> {
    companion object {
        fun <T> ok(): ValidationResult<T> = Ok
        fun <T> error(value: T): ValidationResult<T> = Error(value)
    }

    abstract val get: T
    abstract val isOk: Boolean
    abstract val isError: Boolean

    val asOption: Option<T>
        get() = when (this) {
            is Error -> Option.pure(get)
            is Ok -> Option.none()
        }

    fun onError(block: (T) -> Unit) {
        when (this) {
            is Error -> block(get)
            is Ok -> Unit
        }
    }

    fun <R> map(block: (T) -> R): ValidationResult<R> = flatMap {
        Error(block(it))
    }

    fun <R> flatMap(block: (T) -> ValidationResult<R>): ValidationResult<R> = when (this) {
        is Ok -> this
        is Error -> block(this.value)
    }

    object Ok : ValidationResult<Nothing>() {
        override val get: Nothing = throw NoSuchElementException("Validation result does not contain error.")
        override val isOk: Boolean = true
        override val isError: Boolean = !isOk
    }

    data class Error<out T>(val value: T) : ValidationResult<T>() {
        override val get: T = value
        override val isOk: Boolean = false
        override val isError: Boolean = !isOk
    }
}
