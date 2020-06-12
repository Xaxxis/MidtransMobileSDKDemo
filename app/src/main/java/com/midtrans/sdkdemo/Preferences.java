package com.midtrans.sdkdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

    static final String KEY_VALUE = "myKey";


    private static SharedPreferences getSharedPreference(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setValue(Context context, String value){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_VALUE, value);
        editor.apply();
    }


    public static String getValue(Context context){
        return getSharedPreference(context).getString(KEY_VALUE,"valueIsEmpty");
    }
}
