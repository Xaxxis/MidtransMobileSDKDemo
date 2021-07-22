package com.midtrans.sdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.midtrans.sdk.corekit.callback.TransactionCallback;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.UIKitCustomSetting;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.TransactionResponse;
import com.midtrans.sdk.corekit.models.snap.Authentication;
import com.midtrans.sdk.corekit.models.snap.CreditCard;
import com.midtrans.sdk.corekit.models.snap.CreditCardPaymentModel;
import com.midtrans.sdk.corekit.models.snap.Gopay;
import com.midtrans.sdk.corekit.models.snap.Shopeepay;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import java.sql.Timestamp;

public class MainActivity extends AppCompatActivity {

    private Button btnPayment, btnWithToken, btnViewSp;
    private EditText edtSnapToken, edtClientKey;
    private MidtransSDK midtransSDK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();

        Preferences.setValue(this, "TEST-SHARED-PREFERENCE");

        btnPayment.setOnClickListener(v -> payTransaction());
    }


    private void startCustomFinishPage() {
        Intent intent = new Intent(this, CustomFinishPageActivity.class);
        startActivity(intent);
    }

    private void bindView() {
        btnPayment = findViewById(R.id.btn_snapAuto);
    }

    private void initializeMidtransUiKitSdk(String clientKey) {
        SdkUIFlowBuilder.init()
                .setClientKey(clientKey) // client_key is mandatory
                .setContext(this) // context is mandatory
                .setMerchantBaseUrl("https://sample-demo-dot-midtrans-support-tools.et.r.appspot.com/") //set merchant url (required)
                .enableLog(true) // enable sdk log (optional)
                .setColorTheme(new CustomColorTheme("#2F80C2", "#07ADDC", "#88C7E8")) // set theme. it will replace theme on snap theme on MAP ( optional)
                .buildSDK();
    }

    public void onFinishedTransaction(TransactionResult result) {
        if (result.getResponse() != null) {
            switch (result.getStatus()) {
                case TransactionResult.STATUS_SUCCESS:
                    Toast.makeText(this, "Transaction Finished. ID: " + result.getResponse().getTransactionId() + result.getResponse().getMaskedCard() + result.getResponse().getBank(), Toast.LENGTH_LONG).show();
                    System.out.println("TR MASK-CARD: "+result.getResponse().getMaskedCard());
                    System.out.println("TR APPROVAL CODE: "+result.getResponse().getApprovalCode());
                    System.out.println("TR BANK: "+result.getResponse().getBank());
                    break;
                case TransactionResult.STATUS_PENDING:
                    Toast.makeText(this, "Transaction Pending. ID: " + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    break;
                case TransactionResult.STATUS_FAILED:
                    Toast.makeText(this, "Transaction Failed. ID: " + result.getResponse().getTransactionId() + ". Message: " + result.getResponse().getStatusMessage(), Toast.LENGTH_LONG).show();
                    break;
            }
            result.getResponse().getValidationMessages();
        } else if (result.isTransactionCanceled()) {
            //TODO: Cancel TRX to your backend
            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show();
        } else {
            if (result.getStatus().equalsIgnoreCase(TransactionResult.STATUS_INVALID)) {
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Transaction Finished with failure.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /*
     * Show snap with detail request from mobile SDK. The mobile SDK will request the snap token with detail to merchant backend
     */
    private void payTransaction() {
        initializeMidtransUiKitSdk("SB-Mid-client-nKsqvar5cn60u2Lv");
        midtransSDK = MidtransSDK.getInstance();


        CreditCard creditCard = new CreditCard();
        creditCard.setAuthentication(Authentication.AUTH_3DS);
        creditCard.setSaveCard(true);


        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String orderId = "MidSampleSDK-"+timestamp.getTime();
        TransactionRequest transactionRequest = new TransactionRequest(orderId, 1);
        transactionRequest.setCreditCard(creditCard);
        transactionRequest.setGopay(new Gopay("midtrans://demo"));
        transactionRequest.setShopeepay(new Shopeepay("midtrans://demo"));


        midtransSDK.setTransactionRequest(transactionRequest);
        midtransSDK.startPaymentUiFlow(this);
    }



}