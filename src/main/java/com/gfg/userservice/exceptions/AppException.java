package com.gfg.userservice.exceptions;

import org.springframework.http.HttpStatusCode;

public class AppException extends RuntimeException{

    private final String errorCode;
    private final Object[] args;

    public AppException(String errorCode, Object... args) {
        this.errorCode = errorCode;
        this.args = args;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getArgs() {
        return args;
    }
}
