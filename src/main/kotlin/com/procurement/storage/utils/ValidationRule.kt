package com.procurement.storage.utils

typealias ValidationRule<T, E> = (T) -> ValidationResult<E>
