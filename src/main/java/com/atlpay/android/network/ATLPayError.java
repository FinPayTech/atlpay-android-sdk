package com.atlpay.android.network;

import com.atlpay.android.constants.ErrorTypes;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by OneOSloution on 8/10/2017.
 */

public class ATLPayError extends ErrorTypes {
    public String message;
    public String errorCode;
    public String declineCode;

    public void parseRequestFailed(Request request){
        //System.out.println("Token Body " + request.responseBody);
        //System.out.println("Token Code " + request.responseCode);
        try {
            JSONObject errorResponse    =   new JSONObject(request.responseBody);
            if(errorResponse.has("message")) {
                this.message = errorResponse.getString("message");
            }else{
                this.message = errorResponse.getString("message");
            }
            if(errorResponse.has("error")){
                JSONObject errorObj =   errorResponse.getJSONObject("error");
                this.errorCode  =   errorObj.getString("code");
                if(errorObj.has("message")){
                    this.message =   errorObj.getString("message");
                }
                if(errorObj.has("decline_reason")){
                    declineCode =   errorObj.getString("decline_reason");
                }
            }
        } catch (JSONException e) {
            this.message    =   "Invalid Request Error Encountered.";
            e.printStackTrace();
        }
    }
    public ATLPayError(Request request){
        if(request.responseCode == 400){
            this.serverErrorType  =   "BAD_REQUEST_ERROR";
            parseRequestFailed(request);
        }else if(request.responseCode == 401){
            this.serverErrorType  =   "AUTHORIZATION_ERROR";
            this.message    =   "Authorization Error on ATLPay.";
        }else if(request.responseCode == 403){
            this.serverErrorType  =   "AUTHENTICATION_ERROR";
            this.message    =   "Authentication Error on ATLPay.";
        }else if(request.responseCode == 404){
            this.serverErrorType  =   "OBJECT_NOT_FOUND_ERROR";
            this.message    =   "Object was not found on ATLPay Server.";
        }else if(request.responseCode == 405){
            this.serverErrorType  =   "METHOD_NOT_ALLOWED_ERROR";
            this.message    =   "This method is not allowed for this request.";
        }else if(request.responseCode == 500){
            this.serverErrorType  =   "SERVER_ERROR";
            this.message    =   "ATLPay Server Error Encountered.";
        }else{
            this.serverErrorType  =   "UNKNOWN_ERROR";
            this.message    =   "Something went wrong, Beyond ATLPay";
        }
    }
}
