package com.procurement.storage.exception;

import lombok.Getter;

@Getter
public class GetFileException extends RuntimeException {

    private final String message;

    public GetFileException(final String message) {
        this.message = message;
    }
}
