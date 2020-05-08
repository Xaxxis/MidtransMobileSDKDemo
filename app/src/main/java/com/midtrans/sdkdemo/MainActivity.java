package com.midtrans.sdkdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.UIKitCustomSetting;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.snap.Authentication;
import com.midtrans.sdk.corekit.models.snap.CreditCard;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import java.util.UUID;

import static com.midtrans.sdkdemo.BuildConfig.BASE_URL;
import static com.midtrans.sdkdemo.BuildConfig.CLIENT_KEY;

public class MainActivity extends AppCompatActivity {

    private Button btnPayment, btnWithToken;
    private EditText edtSnapToken, edtClientKey;
    private MidtransSDK midtransSDK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payTransaction();
            }
        });
        btnWithToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payWithToken();
            }
        });
    }

    private void bindView() {
        btnPayment = findViewById(R.id.btn_snapAuto);
        btnWithToken = findViewById(R.id.btn_withToken);
        edtSnapToken = findViewById(R.id.edt_snap_token);
        edtClientKey = findViewById(R.id.edt_clientKey);
    }

    private void initializeMidtransUiKitSdk(String clientKey) {
        SdkUIFlowBuilder.init()
                .setClientKey(clientKey) // client_key is mandatory
                .setContext(this) // context is mandatory
                .setMerchantBaseUrl(BASE_URL) //set merchant url (required)
                .enableLog(true) // enable sdk log (optional)
                .setColorTheme(new CustomColorTheme("#FFE51255", "#B61548", "#FFE51255")) // set theme. it will replace theme on snap theme on MAP ( optional)
                .buildSDK();
    }

    /*
     * Show snap with detail request from mobile SDK. The mobile SDK will request the snap token with detail to merchant backend
     */
    private void payTransaction() {
        initializeMidtransUiKitSdk(CLIENT_KEY);
        midtransSDK = MidtransSDK.getInstance();

        UIKitCustomSetting setting = new UIKitCustomSetting();
        setting.setSkipCustomerDetailsPages(true);
        midtransSDK.setUIKitCustomSetting(setting);

        CreditCard creditCard = new CreditCard();
        creditCard.setAuthentication(Authentication.AUTH_3DS);


        final UUID idRand = UUID.randomUUID();
        TransactionRequest transactionRequest = new TransactionRequest(idRand.toString(), 202020);
        transactionRequest.setCreditCard(creditCard);

        midtransSDK.setTransactionRequest(transactionRequest);
        midtransSDK.startPaymentUiFlow(this);

    }


    /*
        If you want show Snap with only snap token
     */
    private void payWithToken() {
        initializeMidtransUiKitSdk(edtClientKey.getText().toString());
        midtransSDK = MidtransSDK.getInstance();

        UIKitCustomSetting setting = new UIKitCustomSetting();
        setting.setSkipCustomerDetailsPages(true);
        midtransSDK.setUIKitCustomSetting(setting);

        // New object CC for set transaction is 3DS
        CreditCard creditCard = new CreditCard();
        creditCard.setAuthentication(Authentication.AUTH_3DS);

        final UUID idRand = UUID.randomUUID();
        TransactionRequest transactionRequest = new TransactionRequest(idRand.toString(), 20202020);
        transactionRequest.setCreditCard(creditCard);

        Editable token = edtSnapToken.getText();

        // The snap token from your backend. You need get snap token request first from your backend before use this method.
        midtransSDK.startPaymentUiFlow(this, token.toString());
    }
}
