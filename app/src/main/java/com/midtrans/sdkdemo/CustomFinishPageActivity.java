package com.midtrans.sdkdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.Bundle;

public class CustomFinishPageActivity extends AppCompatActivity {

    AppCompatTextView tvValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_finish_page);
        tvValue = findViewById(R.id.tv_value);
        String valueSp = Preferences.getValue(this);
        tvValue.setText(valueSp);
    }
}