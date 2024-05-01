package com.xing.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public  class PrivateSaveUtil {
    private static SharedPreferences sharedPreferences;

    public static void init(Context context){
        sharedPreferences = context.getSharedPreferences("share",Context.MODE_PRIVATE);
    }
    public static void saveString(String key,String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static void saveInt(String key,int value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public static void saveBoolean(String key,Boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String getString(String key){
        String value=sharedPreferences.getString(key,"");
        return  value;
    }
    public static int getInt(String key){
        int value=sharedPreferences.getInt(key,-1);
        return  value;
    }
    public static boolean getBoolean(String key){
        boolean value=sharedPreferences.getBoolean(key,false);
        return  value;
    }
}
