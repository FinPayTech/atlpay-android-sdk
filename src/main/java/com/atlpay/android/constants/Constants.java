package com.atlpay.android.constants;

import android.content.Context;
import android.os.Vibrator;
import android.widget.Toast;

import static android.content.Context.VIBRATOR_SERVICE;

public class Constants {
    public static String endPoint=	"https://api.atlpay.com/v2";
    public static String uniqueUUID = "uniqueUUID";
    public static String IPADDRESS = "ipaddress";
    private static void vibrate(Context mContext) {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(500);
    }
    public static void displayToast(Context mContext, String message, boolean vibrate){
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        if(vibrate == true) {
            vibrate(mContext);
        }
    }
}






