package com.ioter.eastsoft.common.exception;


public class ApiException extends BaseException {


    public ApiException(int code, String displayMessage) {
        super(code, displayMessage);
    }
}
