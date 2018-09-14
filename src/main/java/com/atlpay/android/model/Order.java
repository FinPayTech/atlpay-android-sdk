package com.atlpay.android.model;

import android.content.Context;
import android.util.Log;

import com.atlpay.android.constants.Constants;
import com.atlpay.android.network.ATLPayError;
import com.atlpay.android.network.NetworkRequest;
import com.atlpay.android.network.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

/**
 * Created by OneOSloution on 8/10/2017.
 */

public class Order implements ATLPayObserver {
    String orderNumber;
    String description;
    String currency;
    String tokenId;
    String authUrl;
    String status;
    Double amount;
    URL returnUrl;
    String reason;
    String id;
    ATLPayObserver atlPayObserver;
    Context mContext;

    public String getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public Order(Context mContext){
        this.mContext   =   mContext;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public URL getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(URL returnUrl) {
        this.returnUrl = returnUrl;
    }

    public void refreshOrder(JSONObject response){
        try {
            id              =   response.getString("id");
            if(response.has("txn_reference"))
                orderNumber     =   response.getString("txn_reference");
            if(response.has("amount"))
                amount          =   response.getDouble("amount") / 100;
            if(response.has("currency"))
                currency        =   response.getString("currency");
            if(response.has("status"))
                status          =   response.getString("status");
            if(response.has("reason"))
                reason          =   response.getString("reason");
            if(response.has("threedSecure")) {
                JSONObject threedObject = response.getJSONObject("threedSecure");
                authUrl = threedObject.getString("redirect");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void get(String chargeId, ATLPayObserver atlPayObserver){
        this.atlPayObserver = atlPayObserver;
        Request orderRequest    =   new Request(this.mContext);
        String endPoint = Constants.endPoint + "/charges/"+chargeId;
        orderRequest.sendPayload(Request.Method.GET, endPoint,  null, new NetworkRequest(){
            @Override
            public void onSuccess(JSONObject response) {
                refreshOrder(response);
                onRequestSuccess();
            }

            @Override
            public ATLPayError onFailure(ATLPayError atlPayError) {
                onRequestFailure(atlPayError);
                return null;
            }
        });
    }

    public void create(ATLPayObserver atlPayObserver){
        this.atlPayObserver = atlPayObserver;
        HashMap<String, String> payloadParams  =   new HashMap<String, String>();
        payloadParams.put("token", this.getTokenId());
        payloadParams.put("description", this.getDescription());
        payloadParams.put("txn_reference", this.getOrderNumber());
        payloadParams.put("amount", String.valueOf((int)(this.getAmount()*100)));
        payloadParams.put("currency", this.getCurrency());
        if(null != this.getReturnUrl()) {
            payloadParams.put("return_url", this.getReturnUrl().toString());
        }
        Request orderRequest    =   new Request(this.mContext);
        String endPoint = Constants.endPoint + "/charges";
        orderRequest.sendPayload(Request.Method.POST, endPoint,  payloadParams, new NetworkRequest(){
            @Override
            public void onSuccess(JSONObject response) {
                refreshOrder(response);
                onRequestSuccess();
            }
            @Override
            public ATLPayError onFailure(ATLPayError atlPayError) {
                onRequestFailure(atlPayError);
                return null;
            }
        });
    }

    public void capture(String chargeId, ATLPayObserver atlPayObserver){
        this.atlPayObserver = atlPayObserver;
        HashMap<String, String> payloadParams  =   new HashMap<String, String>();
        Request captureRequest    =   new Request(this.mContext);
        String endPoint = Constants.endPoint+"/charges/capture/"+chargeId;
        Log.d("CaptureURL", endPoint);
        captureRequest.sendPayload(Request.Method.POST, endPoint,  payloadParams, new NetworkRequest(){
            @Override
            public void onSuccess(JSONObject response) {
                refreshOrder(response);
                onRequestSuccess();
            }

            @Override
            public ATLPayError onFailure(ATLPayError atlPayError) {
                onRequestFailure(atlPayError);
                return null;
            }
        });
    }

    @Override
    public void onRequestSuccess() {
        atlPayObserver.onRequestSuccess();
    }

    @Override
    public void onRequestFailure(ATLPayError atlPayError) {
        atlPayObserver.onRequestFailure(atlPayError);
    }

}