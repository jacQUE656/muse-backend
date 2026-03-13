package com.example.musebackend.Exceptions;

public class UserAlreadyExistsException extends Exception{
    public UserAlreadyExistsException(String message) {
        super(message);
    }

}
