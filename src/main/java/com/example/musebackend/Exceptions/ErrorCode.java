package com.example.musebackend.Exceptions;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND),
    TOKEN_NOT_FOUND("TOKEN_NOT_FOUND", "Token not found", HttpStatus.NOT_FOUND),

    CHANGE_PASSWORD_MISMATCH("CHANGE_PASSWORD_MISMATCH", "Current password and new password does not match", HttpStatus.BAD_REQUEST),

    INVALID_CURRENT_PASSWORD("INVALID_CURRENT_PASSWORD", "Current Password is invalid", HttpStatus.BAD_REQUEST),
    ACCOUNT_ALREADY_DEACTIVATED("ACCOUNT_ALREADY_DEACTIVATED", "User account already deactivated",HttpStatus.BAD_REQUEST ),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email already exists",HttpStatus.BAD_REQUEST),
    PHONE_ALREADY_EXISTS("PHONE_ALREADY_EXISTS", "Phone number already exists" , HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("PASSWORD_MISMATCH" , "Password do not match", HttpStatus.BAD_REQUEST),
    ERR_USR_DISABLED("ERR_USR_DISABLED" , "User is disabled", HttpStatus.UNAUTHORIZED),
    BAD_CREDENTIALS("BAD_CREDENTIALS","Username and / password incorrect", HttpStatus.UNAUTHORIZED),
    USERNAME_NOT_FOUND("USERNAME_NOT_FOUND", "Username not found", HttpStatus.NOT_FOUND),
    INTERNAL_EXCEPTION("INTERNAL_EXCEPTION", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR)
    ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;


    ErrorCode(String code, String defaultMessage, HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}