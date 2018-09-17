package com.atlpay.android.ui;

import android.content.Intent;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.atlpay.android.R;

public class SecurePayment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        Intent intent = getIntent();
        Uri data = intent.getData();
        setContentView(R.layout.activity_secure_payment);
        final WebView webView =  (WebView) findViewById(R.id.threeDSecure);
        final LinearLayout mLinear = (LinearLayout) findViewById(R.id.redirectMessage);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                if(progress == 100){
                    mLinear.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                }else{
                    mLinear.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(SecurePayment.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("/3dReturn")) {
                  //As per your requirements here
                    return true;
                }
                return false;
            }
        });
        webView.loadUrl(getIntent().getStringExtra("frameUrl"));
    }

}
