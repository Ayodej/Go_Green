package com.app.gogreen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.app.gogreen.R;
import com.app.gogreen.activities.buyer.BuyerLoginActivity;
import com.app.gogreen.activities.buyer.MainActivity;
import com.app.gogreen.activities.seller.AdminLoginScreenActivity;
import com.app.gogreen.databinding.ActivitySelectionBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SelectionActivity extends AppCompatActivity {

    ActivitySelectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_selection);

        binding.btnBuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BuyerLoginActivity.class));
            }
        });
        binding.btnSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AdminLoginScreenActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }
}