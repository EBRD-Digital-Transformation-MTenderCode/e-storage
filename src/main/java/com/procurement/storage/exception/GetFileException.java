package com.procurement.storage.exception;

import lombok.Getter;

@Getter
public class GetFileException extends RuntimeException {

    private String message;

    public GetFileException(String message) {
        this.message = message;
    }
}
