package com.atlpay.android.constants;

public class ErrorTypes{
    public static final String AuthorizationError          =    "AUTHORIZATION_ERROR";
    public static final String InvalidRequestError         =    "INVALID_REQUEST_ERROR";
    public static final String BadRequestError             =    "BAD_REQUEST_ERROR";
    public static final String AuthenticationError         =    "AUTHENTICATION_ERROR";
    public static final String ObjectNotFoundError         =    "OBJECT_NOT_FOUND_ERROR";
    public static final String MethodNotAllowed            =    "METHOD_NOT_ALLOWED_ERROR";
    public static final String ServerError                 =    "SERVER_ERROR";
    public static final String UnknownError                =    "UNKNOWN_ERROR";
    public String serverErrorType;
    public boolean isAuthorizationError(){
        return AuthorizationError   == this.serverErrorType;
    }
    public boolean isInvalidRequestError(){
        return InvalidRequestError   == this.serverErrorType;
    }
    public boolean isAuthenticationError(){
        return AuthenticationError   == this.serverErrorType;
    }
    public boolean isObjectNotFoundError(){
        return ObjectNotFoundError   == this.serverErrorType;
    }
    public boolean isMethodNotAllowed(){
        return MethodNotAllowed   == this.serverErrorType;
    }
    public boolean isServerError(){
        return ServerError  == this.serverErrorType;
    }
    public boolean isBadRequestError(){
        return BadRequestError  == this.serverErrorType;
    }
    public boolean isUnknownError(){
        return UnknownError   == this.serverErrorType;
    }
}
