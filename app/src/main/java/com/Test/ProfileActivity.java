package com.Test;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;


/**
 * The Profile activity.
 * Profile Page will have few input and image picker,
 * It will have email sent from previous screen and it will be shown under email input.
 *
 * @author
 */
public class ProfileActivity extends AppCompatActivity {


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int MY_CAMERA_REQUEST_CODE = 13;
    private static final String ACTIVITY_NAME = "PROFILE_ACTIVITY";
    SharedPreferences prefs;
    EditText email;
    ImageButton mImageButton;
    private Locale locale;
    Button mButtonChat;
    Button weatherForcasting;
    Button GoToTestToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        initData();
    }

    void initData() {
        mImageButton = findViewById(R.id.btnTakePic);
        email = findViewById(R.id.edtEmail);
        mButtonChat= findViewById(R.id.buttonChat);
        weatherForcasting= findViewById(R.id.weatherForcasting);
        //Initialized the button
        GoToTestToolBar = findViewById(R.id.goToTestToolBar);


        prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        Log.e(ACTIVITY_NAME, "In function:onCreate");
        if (getIntent() != null) {
            String receivedEmail = getIntent().getStringExtra("Email");
            if (!TextUtils.isEmpty(receivedEmail)) {
                email.setText(receivedEmail);
            }
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
        GoToTestToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),TestToolBar.class);
                startActivity(intent);
            }
        });

        // click listern for button to open another screen
        mButtonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open chat screen
                Intent mIntent =new Intent(ProfileActivity.this,ChatRoomActivity.class);
                startActivity(mIntent);
            }
        });

        weatherForcasting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent =new Intent(ProfileActivity.this,WeatherForecast.class);
                startActivity(mIntent);            }
        });
    }


    /**
     * Function will be call on to capture picture from device camera,
     * Also it validate the device camera permission is allowed or not
     * if not allowed then it will request user to allow permission to proceed
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void dispatchTakePictureIntent(View view) {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(ACTIVITY_NAME, "In function:onActivityResult");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageButton.setImageBitmap(imageBitmap);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(ACTIVITY_NAME, "In Function:" + "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(ACTIVITY_NAME, "In Function:" + "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(ACTIVITY_NAME, "In Function:" + "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(ACTIVITY_NAME, "In Function:" + "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(ACTIVITY_NAME, "In Function:" + "onDestroy()");
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