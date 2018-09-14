package com.atlpay.android.model;
import com.atlpay.android.network.ATLPayError;

public interface ATLPayObserver {
    void onRequestSuccess();
    void onRequestFailure(ATLPayError atlPayError);
}
