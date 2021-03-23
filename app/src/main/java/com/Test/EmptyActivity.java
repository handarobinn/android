package com.Test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        Bundle bundle = getIntent().getBundleExtra("message");
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        DetailsFragment fragment = new DetailsFragment();
        if(bundle!=null){
            fragment.setArguments(bundle);
        }

        transaction.replace(R.id.message_frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}