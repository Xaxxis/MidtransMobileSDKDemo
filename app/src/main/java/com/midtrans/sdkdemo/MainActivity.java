package com.midtrans.sdkdemo;

import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.UIKitCustomSetting;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.snap.Authentication;
import com.midtrans.sdk.corekit.models.snap.CreditCard;
import com.midtrans.sdk.corekit.models.snap.Gopay;
import com.midtrans.sdk.corekit.models.snap.Shopeepay;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import java.sql.Timestamp;

public class MainActivity extends AppCompatActivity {

    private Button btnPayment, btnWithToken;
    private EditText edtSnapToken;
    private MidtransSDK midtransSDK;
    private static final int GROSS_AMOUNT = 10000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindView();

        btnPayment.setOnClickListener(v -> payTransaction());
        btnWithToken.setOnClickListener(v -> payWithToken());
    }

    private void bindView() {
        btnPayment = findViewById(R.id.btn_snapAuto);
        btnWithToken = findViewById(R.id.btn_snapManual);

        TextView tvGrossAmount = findViewById(R.id.tv_gross_amount);
        tvGrossAmount.setText(String.valueOf(GROSS_AMOUNT));

        edtSnapToken = findViewById(R.id.et_snap_token);
    }

    private void initializeMidtransUiKitSdk() {
        SdkUIFlowBuilder.init()
                .setClientKey("SB-Mid-client-nKsqvar5cn60u2Lv") // client_key is mandatory
                .setContext(this) // context is mandatory
                .setMerchantBaseUrl("https://sample-demo-dot-midtrans-support-tools.et.r.appspot.com/") // set merchant url (required)
                .enableLog(true) // enable sdk log (optional)
                .setColorTheme(new CustomColorTheme("#2F80C2", "#07ADDC", "#88C7E8")) // set theme. it will replace theme on snap theme on MAP (optional)
                .buildSDK();
    }

    /*
     * Show snap with detail request from mobile SDK.
     * The mobile SDK will request the snap token with detail to merchant backend.
     * Details request: https://mobile-docs.midtrans.com/#prepare-transaction-details
     */
    private void payTransaction() {
        // 1. initialize Midtrans SDK
        initializeMidtransUiKitSdk();
        midtransSDK = MidtransSDK.getInstance();

        // 2. Set Custom UI Based on your needs
        UIKitCustomSetting setting = new UIKitCustomSetting();
        setting.setSkipCustomerDetailsPages(true);
        setting.setShowEmailInCcForm(true);
        setting.setShowPaymentStatus(true);
        midtransSDK.setUIKitCustomSetting(setting);

        // 3. Prepare the transaction details
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String orderId = "MidSampleSDK-"+timestamp.getTime();
        TransactionRequest transactionRequest = new TransactionRequest(orderId, GROSS_AMOUNT);

        // set credit card setting to set the transactions with 3DS
        // also you can enable feature saved_card from this object
        CreditCard creditCard = new CreditCard();
        creditCard.setAuthentication(Authentication.AUTH_3DS);
        transactionRequest.setCreditCard(creditCard);

        // Set deeplink callback URL for e-money transactions
        transactionRequest.setGopay(new Gopay("midtrans://demo"));
        transactionRequest.setShopeepay(new Shopeepay("midtrans://demo"));
        midtransSDK.setTransactionRequest(transactionRequest);

        // 4. Do a request and Open the payment SDK
        midtransSDK.startPaymentUiFlow(this);
    }


    /*
    * This method open Payment screen with manual snap token request from your backend
    * without Midtrans SDK. You can find the details of request from this page
    * Snap Docs: https://snap-docs.midtrans.com/#request-body-json-parameter
    */
    private void payWithToken() {
        // 1. Get snap token value from UI
        Editable snapToken = edtSnapToken.getText();

        // 2. initialize Midtrans SDK
        initializeMidtransUiKitSdk();
        midtransSDK = MidtransSDK.getInstance();

        // 3. Set Custom UI Based on your needs
        UIKitCustomSetting setting = new UIKitCustomSetting();
        setting.setSkipCustomerDetailsPages(true);
        setting.setShowEmailInCcForm(true);
        setting.setShowPaymentStatus(true);
        midtransSDK.setUIKitCustomSetting(setting);

        // 4. Open the payment SDK
        midtransSDK.startPaymentUiFlow(this, snapToken.toString());
    }

}