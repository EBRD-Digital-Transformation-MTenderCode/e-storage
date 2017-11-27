package com.procurement.storage.exception;

import lombok.Getter;

@Getter
public class ReservFileValidationException extends RuntimeException {

    private String message;

    public ReservFileValidationException(String message) {
        this.message = message;
    }
}
