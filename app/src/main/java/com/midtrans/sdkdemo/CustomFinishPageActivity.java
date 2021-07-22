package com.midtrans.sdkdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class CustomFinishPageActivity extends AppCompatActivity {

    AppCompatTextView tvValue;
    AppCompatButton btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_finish_page);
        tvValue = findViewById(R.id.tv_status);
        btnFinish = findViewById(R.id.btn_finish);


        Intent intent = getIntent();
        Uri data = intent.getData();
//        String result = data.getQueryParameter("result");
//
//        tvValue.setText(result.toUpperCase());



        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
            }
        });
    }
}