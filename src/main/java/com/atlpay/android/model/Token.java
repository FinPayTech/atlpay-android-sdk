package com.atlpay.android.model;

import android.content.Context;
import android.util.Log;

import com.atlpay.android.constants.Constants;
import com.atlpay.android.network.ATLPayError;
import com.atlpay.android.network.NetworkRequest;
import com.atlpay.android.network.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by OneOSloution on 8/10/2017.
 */

public class Token implements ATLPayObserver {
    private String id, cardBrand, cardCountry, fundingType, mode, last4Digits, redirectStatus;
    ATLPayObserver atlPayObserver;
    Context mContext;

    public Token(Context mContext){
        this.mContext   =   mContext;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public String getCardCountry() {
        return cardCountry;
    }

    public String getFundingType() {
        return fundingType;
    }

    public String getMode() {
        return mode;
    }

    public String getLast4Digits() {
        return last4Digits;
    }

    public String getRedirectStatus(){
        return this.redirectStatus;
    }

    public void create(Card card,String ipAddress,String uuid,String userEmailId, ATLPayObserver atlPayObserver){
        this.atlPayObserver = atlPayObserver;
        HashMap<String, String> payloadParams  =   new HashMap<String, String>();
        payloadParams.put("card[number]",card.getCardNumber());
        payloadParams.put("card[exp_month]", String.valueOf(card.getExpMonth()));
        payloadParams.put("card[exp_year]", String.valueOf(card.getExpYear()));
        payloadParams.put("card[cvc]", card.getCvc());
        payloadParams.put("card[name]", card.getName());
        payloadParams.put("address[address_line1]", card.getAddressLine1());
        payloadParams.put("address[address_line2]", card.getAddressLine2());
        payloadParams.put("address[city]", card.getCity());
        payloadParams.put("address[state]", card.getState());
        payloadParams.put("address[country]", card.getCountry());
        payloadParams.put("address[zipcode]", card.getZipcode());
        payloadParams.put("shopper[ip]", ipAddress);
        payloadParams.put("shopper[session_id]",uuid);
        payloadParams.put("shopper[email]",userEmailId);
        Request tokenRequest    =   new Request(this.mContext);
        String endPoint = Constants.endPoint + "/tokens";
        Log.d("PaymentPayloadParams",payloadParams.toString());
        Log.d("PaymentEndPoint",endPoint.toString());

        tokenRequest.sendPayload(Request.Method.POST,endPoint,payloadParams,new NetworkRequest(){
            @Override
            public void onSuccess(JSONObject response) {
                read(response);
            }
            @Override
            public ATLPayError onFailure(ATLPayError atlPayError) {
                onRequestFailure(atlPayError);
                return atlPayError;
            }
        });
    }

    public void read(JSONObject tokenObject) {
        try {
            id =   tokenObject.getString("id");
            JSONObject cardObject   =  (JSONObject) tokenObject.get("card");
            cardCountry   =   cardObject.getString("country");
            cardBrand   =   cardObject.getString("brand");
            last4Digits =   cardObject.getString("last4Digits");
            fundingType    =   cardObject.getString("fundingType");
            redirectStatus    =   cardObject.getString("authorization");
            onRequestSuccess();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
