package com.rohanharrison.notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;

public class SaveSharedPreference
{
    static final String PREF_USER_NAME = "username";
    static final String PREF_PASSWORD = "password";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName) throws JSONException {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName.toString());
        editor.commit();
    }

    public static void setPassword(Context ctx, String password) throws JSONException {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_PASSWORD, password.toString());
        editor.commit();
    }

    public static void clearUserName(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }



    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static String getPassword(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_PASSWORD, "");
    }
}