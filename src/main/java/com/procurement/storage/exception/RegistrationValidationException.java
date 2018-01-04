package com.procurement.storage.exception;

import lombok.Getter;

@Getter
public class RegistrationValidationException extends RuntimeException {

    private final String message;

    public RegistrationValidationException(final String message) {
        this.message = message;
    }
}
