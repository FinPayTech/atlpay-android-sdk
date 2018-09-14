package com.atlpay.android.network;
import org.json.JSONObject;
public interface NetworkRequest {
    void onSuccess(JSONObject response);
    ATLPayError onFailure(ATLPayError atlPayError);
}
