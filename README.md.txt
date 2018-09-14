# ATLPay Android SDK

The ATLPay Android SDK makes it easy to add atlpay payments to mobile apps.

# How to use
* In your root gradle file do the following :
```java
   allprojects {
     repositories {
	...
	maven { url 'https://jitpack.io' }
	}
   }
```

## Requirements
 minimum Android 4.1 (API Level 16).
# Usage
* Step-1
```java
   Intent intentCardPayment=new Intent(MainActivity.this,CardPayment.class);
                startActivity(intentCardPayment);
```
* Step-2: set your card:

        setCart('PUT_YOUR_AMOUNT','PUT_YOUR_CURRENCY','PUT_YOUR_ORDER_NUMBER','PUT_YOUR_ORDER_DESCRIPTION');
	
	
* Step-3:  Creating a Token:
```java
   ATLPay.setSecretKey('PLACE_YOUR_SECRET_KEY_HERE');
        token = new Token(mContext);
        token.create(card,'PLACE_YOUR_EMAIL_ID_HERE', new ATLPayObserver() {
                    @Override
                    public void onRequestSuccess() {
		    // Everything went well     
                        initOrder();
                    }
                    @Override
                    public void onRequestFailure(ATLPayError atlPayError) {
			//Error Happened
                        Constants.displayToast(mContext, atlPayError.message, true);
                      
                    }
                }
        );	
    }
```
* Step-4:  Creating a Order:
 ```java
   private void initOrder() {
        String tokenId = token.getId();
        order = new Order(CardPayment.this);
        order.setTokenId(tokenId);
        order.setAmount(amount);
        order.setCurrency(currency);
        order.setDescription(orderDescription);
        order.setOrderNumber(orderNumber);
        try {
            if (null != token.getRedirectStatus() && !token.getRedirectStatus().equals("NOT_AVAILABLE")) {
                URL url = new URL("http://com.infinit.com/3dReturn");
                order.setReturnUrl(url);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        order.create(new ATLPayObserver() {
            @Override
            public void onRequestSuccess() {
	     // Everything went well  
                processOrder();
            }
            @Override
            public void onRequestFailure(ATLPayError atlPayError) {
	    //Error Happened
                Constants.displayToast(mContext, atlPayError.message, true);
            }
        });
    }
```

 * Step-5: Capture Order:
 ```java
 public void processOrder() {
        if (token.getRedirectStatus().equals("NOT_AVAILABLE")) {
            order.capture(order.getId(), new ATLPayObserver() {
                @Override
                public void onRequestSuccess() {
		  // Everything went well  
                   //As per your requirement or send to the DataBase or Success page.
                }
                @Override
                public void onRequestFailure(ATLPayError atlPayError) {
                    if (null != atlPayError.errorCode) {
                        if (atlPayError.errorCode == "INVALID_CVC") {
                            Constants.displayToast(mContext, atlPayError.message, true);
                        } else if (atlPayError.errorCode == "INCORRECT_CVC") {
                            Constants.displayToast(mContext, atlPayError.message, true);
                        } else if (atlPayError.errorCode == "INCORRECT_ZIPCODE") {
                            Constants.displayToast(mContext, atlPayError.message, true);
                        } else if (atlPayError.errorCode == "PROCESSING_ERROR") {
                            Constants.displayToast(mContext, atlPayError.message, true);
                        } else if (atlPayError.errorCode == "EXPIRED_CARD") {
                            Constants.displayToast(mContext, atlPayError.message, true);
                        } else if (atlPayError.errorCode == "UNKNOWN_ERROR") {
                            Constants.displayToast(mContext, atlPayError.message, true);
                        } else if (atlPayError.errorCode == "DECLINED") {
                            Constants.displayToast(mContext, "Your Card has been declined. Bank Returned : " + atlPayError.declineCode, true);
                        }
                    } else {
                        Constants.displayToast(mContext, atlPayError.message, true);
                    }
                    Intent mIntent = new Intent(mContext, CardPayment.class);
                    startActivity(mIntent);
                    finish();
                }
            });
        } else {
            Intent intent = new Intent(CardPayment.this, SecurePayment.class);
            intent.putExtra("frameUrl", order.getAuthUrl());
            startActivity(intent);
            finish();
        }
    }
```

* Step-5: SecurePayment:
 ```java
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
```


## Error Handling

ATLPay uses conventional HTTP response codes to indicate the success or failure of an API request. In general: Codes in the `2xx` range indicate success. Codes in the `4xx` range indicate an error that failed given the information provided (e.g., a required parameter was omitted, a charge failed, etc.). Codes in the `5xx` range indicate an error with ATLPay's servers (these are rare).

Some `4xx` errors that could be handled programmatically (e.g., a card is declined) include an error code that briefly explains the error reported.

#### HTTP status code summary
HTTP Status Code | Description
--- | ---
200 | Everything worked as expected.
400 | The request was unacceptable, often due to missing a required parameter.
401 | No valid API key provided.
402 | The parameters were valid but the request failed.
403 | You are not authorized to perform this transaction.
404 | The requested resource doesn't exist.
500, 502, 503, 504 | Something went wrong on ATLPay's end. (These are rare.)

## Test Cards

Following test cards can be used for testing ATLPay API

Card Number | Brand | Funding Type | Issuer Country
--- | --- | --- | ---
5555 5555 5555 4444 | MasterCard | Credit | GB
5454 5454 5454 5454 | MasterCard | Debit | FR
5555 5555 5555 4443 | MasterCard | Credit | GB
5454 5454 5454 5453 | MasterCard | Debit | GB
6759 6498 2643 8450 | Maestro | Debit | GB
4444 3333 2222 1111 | Visa | Credit | FR
4462 0300 0000 0000 | Visa | Debit | IN
4444 3333 2222 1112 | Visa | Credit | FR
4462 0300 0000 0001 | Visa | Debit | FR
