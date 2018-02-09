package com.procurement.storage.exception;

import lombok.Getter;

@Getter
public class PublishFileException extends RuntimeException {

    private final String message;

    public PublishFileException(final String message) {
        this.message = message;
    }
}
