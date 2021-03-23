package com.Test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

/**
 * The Main activity.
 * Launcher screen of the application that shows application with some field inputs
 * This page will redirect you to profile page
 *
 * @author
 */
public class MainActivity extends AppCompatActivity {
    SharedPreferences prefs;
    EditText email;
    private Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }

    void initData() {
        prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        email = findViewById(R.id.edtEmail);
        String getPrevEmail = prefs.getString("Email", "");
        if (!TextUtils.isEmpty(getPrevEmail)) {
            email.setText(getPrevEmail);
        }
        Configuration config = getResources().getConfiguration();
        String lang = prefs.getString("LANGUAGE_ID", "en");
        String systemLocale = getSystemLocale(config).getLanguage();
        if (!TextUtils.isEmpty(lang) && systemLocale.equalsIgnoreCase(lang)) {
            locale = new Locale(lang);
            Locale.setDefault(locale);
            setSystemLocale(config, locale);
            updateConfiguration(config);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.edit().putString("Email", email.getText().toString()).apply();
    }

    /**
     * Function will be call on Submit button to redirect on profile page
     */
    public void onSubmit(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("Email", email.getText().toString());
        startActivity(intent);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (locale != null) {
            setSystemLocale(newConfig, locale);
            Locale.setDefault(locale);
            updateConfiguration(newConfig);
        }
    }

    private void updateConfiguration(Configuration config) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            createConfigurationContext(config);
        } else {
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
    }

    private Locale getSystemLocale(Configuration config) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return config.getLocales().get(0);
        } else {
            return config.locale;
        }
    }

    private void setSystemLocale(Configuration config, Locale locale) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
    }
}