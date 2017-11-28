package com.procurement.storage.exception;

import lombok.Getter;

@Getter
public class ReservFileValidationException extends RuntimeException {

    private final String message;

    public ReservFileValidationException(final String message) {
        this.message = message;
    }
}
