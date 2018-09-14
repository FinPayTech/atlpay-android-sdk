package com.atlpay.android.view;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;

import com.atlpay.android.R;

/**
 * Created by OneOSloution on 8/17/2017.
 */

public class NameOnCardEditText extends ErrorEditText {
    public NameOnCardEditText(Context context) {
        super(context);
        init();
    }

    public NameOnCardEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NameOnCardEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        InputFilter[] filters = { new InputFilter.LengthFilter(25) };
        setFilters(filters);
    }

    @Override
    public boolean isValid() {
        return isOptional() || getText().toString().length() > 0;
    }

    @Override
    public String getErrorMessage() {
        return getContext().getString(R.string.bt_postal_nameoncard_required);
    }
}
