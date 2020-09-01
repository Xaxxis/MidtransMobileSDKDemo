package com.midtrans.sdkdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.midtrans.sdk.corekit.callback.CardTokenCallback;
import com.midtrans.sdk.corekit.callback.TransactionCallback;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.LocalDataHandler;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.PaymentMethod;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.UIKitCustomSetting;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.CardTokenRequest;
import com.midtrans.sdk.corekit.models.TokenDetailsResponse;
import com.midtrans.sdk.corekit.models.TransactionResponse;
import com.midtrans.sdk.corekit.models.UserAddress;
import com.midtrans.sdk.corekit.models.UserDetail;
import com.midtrans.sdk.corekit.models.snap.Authentication;
import com.midtrans.sdk.corekit.models.snap.CreditCard;
import com.midtrans.sdk.corekit.models.snap.CreditCardPaymentModel;
import com.midtrans.sdk.corekit.models.snap.Gopay;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

import static com.midtrans.sdkdemo.BuildConfig.BASE_URL;
import static com.midtrans.sdkdemo.BuildConfig.CLIENT_KEY;

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

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payWithToken();
            }
        });
//        btnWithToken.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                payWithToken();
//            }
//        });
//        btnViewSp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startCustomFinishPage();
//            }
//        });
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        edtSnapToken.setText("");
//    }

    private void startCustomFinishPage() {
        Intent intent = new Intent(this, CustomFinishPageActivity.class);
        startActivity(intent);
    }

    private void bindView() {
        btnPayment = findViewById(R.id.btn_snapAuto);
//        btnWithToken = findViewById(R.id.btn_withToken);
//        btnViewSp = findViewById(R.id.btn_toCustomFinishPage);
//        edtSnapToken = findViewById(R.id.edt_snap_token);
//        edtClientKey = findViewById(R.id.edt_clientKey);
    }

    private void initializeMidtransUiKitSdk(String clientKey) {
        SdkUIFlowBuilder.init()
                .setClientKey(clientKey) // client_key is mandatory
                .setContext(this) // context is mandatory
                .setMerchantBaseUrl(BASE_URL) //set merchant url (required)
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
        initializeMidtransUiKitSdk(CLIENT_KEY);
        midtransSDK = MidtransSDK.getInstance();

//        UserDetail userDetail = LocalDataHandler.readObject("user_details", UserDetail.class);
//        if (userDetail == null) {
//            userDetail = new UserDetail();
//            userDetail.setUserFullName("Zaki Ibrahim");
//            userDetail.setEmail("ibrahim@mailnesia.com");
//            userDetail.setPhoneNumber("08123456789");
//            // set user ID as identifier of saved card (can be anything as long as unique),
//            // randomly generated by SDK if not supplied
//            userDetail.setUserId("ibrahim-6789");
//
//            ArrayList<UserAddress> userAddresses = new ArrayList<>();
//            UserAddress userAddress = new UserAddress();
//            userAddress.setAddress("Jalan Iskandarsyah");
//            userAddress.setCity("Jakarta");
//            userAddress.setAddressType(com.midtrans.sdk.corekit.core.Constants.ADDRESS_TYPE_BOTH);
//            userAddress.setZipcode("12345");
//            userAddress.setCountry("IDN");
//            userAddresses.add(userAddress);
//            userDetail.setUserAddresses(userAddresses);
//            LocalDataHandler.saveObject("user_details", userDetail);
//        }

        UIKitCustomSetting setting = new UIKitCustomSetting();
        setting.setSkipCustomerDetailsPages(true);
        setting.setShowEmailInCcForm(true);
        setting.setShowPaymentStatus(false);
        midtransSDK.setUIKitCustomSetting(setting);

        midtransSDK.setTransactionFinishedCallback(new TransactionFinishedCallback() {
            @Override
            public void onTransactionFinished(TransactionResult transactionResult) {
                onFinishedTransaction(transactionResult);
            }
        });

        CreditCard creditCard = new CreditCard();
        creditCard.setAuthentication(Authentication.AUTH_3DS);
        creditCard.setSaveCard(true);


        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String orderId = "MidSampleSDK-"+timestamp.getTime();
        TransactionRequest transactionRequest = new TransactionRequest(orderId, 10000);
        transactionRequest.setCreditCard(creditCard);
        transactionRequest.setGopay(new Gopay("demo://midtrans"));

        midtransSDK.setTransactionRequest(transactionRequest);
        midtransSDK.startPaymentUiFlow(this);
    }


    /*
        If you want show Snap with only snap token
     */
    private void payWithToken() {
        initializeMidtransUiKitSdk("SB-Mid-client-nKsqvar5cn60u2Lv");
       midtransSDK = MidtransSDK.getInstance();

        UIKitCustomSetting setting = new UIKitCustomSetting();
        setting.setSkipCustomerDetailsPages(true);
        setting.setShowEmailInCcForm(true);
        setting.setShowPaymentStatus(false);
        midtransSDK.setUIKitCustomSetting(setting);
        midtransSDK.setTransactionFinishedCallback(new TransactionFinishedCallback() {
            @Override
            public void onTransactionFinished(TransactionResult transactionResult) {
                onFinishedTransaction(transactionResult);
            }
        });

//        // New object CC for set transaction is 3DS
//        CreditCard creditCard = new CreditCard();
//        creditCard.setAuthentication(Authentication.AUTH_3DS);
//
//        final UUID idRand = UUID.randomUUID();
//        TransactionRequest transactionRequest = new TransactionRequest(idRand.toString(), 20202020);
//        transactionRequest.setCreditCard(creditCard);
//
////        Editable token = edtSnapToken.getText();
//        midtransSDK.setTransactionRequest(transactionRequest);

        // The snap token from your backend. You need get snap token request first from your backend before use this method.
        midtransSDK.startPaymentUiFlow(this, "6a532a7e-621b-45b6-b5cb-8bdda510ce12");
    }

    private void payWithCoreKit() {

//        CardTokenRequest cardTokenRequest = new CardTokenRequest("4105 0586 8948 1467", "123", "12", "22", CLIENT_KEY);
//
//        MidtransSDK.getInstance().getCardToken(cardTokenRequest, new CardTokenCallback() {
//            @Override
//            public void onSuccess(TokenDetailsResponse response) {
//                // Card token will be used to charge the payment
//                String cardToken = response.getTokenId();
//                // Success action here
//            }
//
//            @Override
//            public void onFailure(TokenDetailsResponse response, String reason) {
//                // Failure action here
//            }
//
//            @Override
//            public void onError(Throwable error) {
//                // Error action here
//            }
//        });


        CreditCardPaymentModel payment = new CreditCardPaymentModel("CARD_TOKEN", false);
        MidtransSDK.getInstance().paymentUsingCard("CHECKOUT_TOKEN", payment, new TransactionCallback() {
            @Override
            public void onSuccess(TransactionResponse response) {
                // Success Action here
            }

            @Override
            public void onFailure(TransactionResponse response, String reason) {
                // Failure Action here
            }

            @Override
            public void onError(Throwable error) {
                // Error Action here
            }
        });


    }

}