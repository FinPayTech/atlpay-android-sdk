package com.atlpay.android.ui;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.atlpay.android.ATLPay;
import com.atlpay.android.OnCardFormSubmitListener;
import com.atlpay.android.R;
import com.atlpay.android.constants.Config;
import com.atlpay.android.constants.Constants;
import com.atlpay.android.model.ATLPayObserver;
import com.atlpay.android.model.Card;
import com.atlpay.android.model.Order;
import com.atlpay.android.model.Token;
import com.atlpay.android.network.ATLPayError;
import com.atlpay.android.utils.CardType;
import com.atlpay.android.view.CardEditText;
import com.atlpay.android.view.CardForm;
import com.atlpay.android.view.SupportedCardTypesView;


import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.Integer.parseInt;

public class CardPayment extends AppCompatActivity implements OnCardFormSubmitListener,
        CardEditText.OnCardTypeChangedListener, View.OnClickListener {
    private static final CardType[] SUPPORTED_CARD_TYPES = {CardType.VISA, CardType.MASTERCARD, CardType.DISCOVER,
            CardType.AMEX, CardType.DINERS_CLUB};
    private SupportedCardTypesView mSupportedCardTypesView;
    private Card card;
    public Token token;
    public Order order;
    Context mContext;
    CardForm mCardForm;
    Button submitBtn;
    String payText;
    String currency;
    double amount;
    String orderNumber;
    String orderDescription;
    String ipAddress;
    String uuid;
    private ProgressDialog mProgressDialog = null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (mCardForm.isCardScanningAvailable()) {
            getMenuInflater().inflate(R.menu.card_io, menu);
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = CardPayment.this;
        card = new Card();
        setContentView(R.layout.activity_payment);
        initFields();
        mSupportedCardTypesView = (SupportedCardTypesView) findViewById(R.id.supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
        mCardForm = findViewById(R.id.card_form);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        mCardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .nameOnCardRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .mobileNumberExplanation("SMS is required on this number")
                .actionLabel("Pay")
                .setup(CardPayment.this);
        mCardForm.setOnCardFormSubmitListener(this);
        mCardForm.setOnCardTypeChangedListener(this);
        // Warning: this is for development purposes only and should never be done outside of this example app.
        // Failure to set FLAG_SECURE exposes your app to screenshots allowing other apps to steal card information.
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setListener();
        ////////PUT_YOUR----->AMOUNT,CURRENCY,ORDER_NUMBER,ORDER_DESCRIPTION <----HERE
        setCart(0.00, "", "", "");
    }
    private void initFields() {
        mProgressDialog = new ProgressDialog(CardPayment.this);
        mProgressDialog.setMessage(getString(R.string.please_wait));
    }
    public void setCart(double amount, String currency, String orderNumber, String orderDescription) {
        this.amount = amount;
        this.currency = currency;
        this.orderNumber = orderNumber;
        this.orderDescription = orderDescription;
    }
    public void setListener() {
        submitBtn.setOnClickListener(this);
    }
    public void resetBtn() {
        submitBtn.setEnabled(true);
    }
    private void submit() {
        mProgressDialog.show();
        mCardForm.closeSoftKeyboard();
        submitBtn.setText(getResources().getString(R.string.please_wait));
        submitBtn.setEnabled(false);
        ATLPay.setSecretKey("");////////PLACE_YOUR_SECRET_KEY_HERE
        token = new Token(mContext);
        //PLACE_EMAIL_ID_HERE..........
        token.create(card,"", new ATLPayObserver() {
                    @Override
                    public void onRequestSuccess() {
                        mProgressDialog.dismiss();
                        initOrder();
                    }
                    @Override
                    public void onRequestFailure(ATLPayError atlPayError) {
                        mProgressDialog.dismiss();
                        Constants.displayToast(mContext, atlPayError.message, true);
                        resetBtn();
                    }
                }
        );
    }
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
                processOrder();
            }
            @Override
            public void onRequestFailure(ATLPayError atlPayError) {
                Constants.displayToast(mContext, atlPayError.message, true);
            }
        });
    }
    public void processOrder() {
        if (token.getRedirectStatus().equals("NOT_AVAILABLE")) {
            order.capture(order.getId(), new ATLPayObserver() {
                @Override
                public void onRequestSuccess() {
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
    @Override
    public void onCardFormSubmit() {
        if (mCardForm.isValid()) {
            card.setCardNumber(mCardForm.getCardNumber());
            card.setExpMonth(parseInt(mCardForm.getExpirationMonth()));
            card.setExpYear(parseInt(mCardForm.getExpirationYear()));
            card.setCvc(mCardForm.getCvv());
            card.setName(mCardForm.getName());
            card.setIpAddress(ipAddress=Config.getIpAddress(mContext));
            card.setUuId(uuid =Config.uniqueID(mContext));
            submit();
        } else {
            mCardForm.validate();
            Toast.makeText(this, R.string.invalid, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onCardTypeChanged(CardType cardType) {
        if (cardType == CardType.EMPTY) {
            mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
        } else {
            mSupportedCardTypesView.setSelected(cardType);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.card_io_item) {
            mCardForm.scanCard(CardPayment.this);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.submitBtn) {
            if (mCardForm.isValid()) {
                card.setCardNumber(mCardForm.getCardNumber());
                card.setExpMonth(parseInt(mCardForm.getExpirationMonth()));
                card.setExpYear(parseInt(mCardForm.getExpirationYear()));
                card.setCvc(mCardForm.getCvv());
                card.setName(mCardForm.getName());
                card.setIpAddress(ipAddress = Config.getIpAddress(mContext));
                card.setUuId(uuid = Config.uniqueID(mContext));
                submit();
            } else {
                mCardForm.validate();
                Toast.makeText(this, R.string.invalid, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
