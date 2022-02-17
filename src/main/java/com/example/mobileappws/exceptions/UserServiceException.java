package com.example.mobileappws.exceptions;

public class UserServiceException extends RuntimeException{

    private static final long serialVersionUID = 4560587038093641525L;

    public UserServiceException(String message) {
        super(message);
    }
}
