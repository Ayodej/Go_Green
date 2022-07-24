package com.app.gogreen.activities.buyer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.app.gogreen.R;
import com.app.gogreen.activities.helpers.BaseActivity;
import com.app.gogreen.activities.seller.AddPlantActivity;
import com.app.gogreen.databinding.ActivityUserLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class BuyerLoginActivity extends BaseActivity {

    private static final String TAG = "EmailPasswordActivity";
    ActivityUserLoginBinding binding;
    String email, passowrd, fcmToken="";
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_login);

        mAuth = FirebaseAuth.getInstance();

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = binding.edtEmail.getText().toString().trim();
                passowrd = binding.editPassword.getText().toString().trim();
                if(email.equals("admin") && passowrd.equals("123")){
                    startActivity(new Intent(getApplicationContext(), AddPlantActivity.class));
                }else {
                    if(!isConnectionAvailable(BuyerLoginActivity.this)){
                        Toast.makeText(BuyerLoginActivity.this, "Check your network!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(TextUtils.isEmpty(email)){
                        binding.edtEmail.setError("Required!");
                        binding.edtEmail.requestFocus();
                        return;
                    }
                    if(TextUtils.isEmpty(passowrd)){
                        binding.editPassword.setError("Required!");
                        binding.editPassword.requestFocus();
                        return;
                    }
                    signIn();
                }
            }
        });
        binding.tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.edtEmail.getText().toString();
                if(TextUtils.isEmpty(email)){
                    binding.edtEmail.setError("Required!");
                    binding.edtEmail.requestFocus();
                    return;
                }
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Password reset email sent successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "Error : "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AuthActivity.class));
            }
        });
    }

    private void signIn() {
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, passowrd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                if (task.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    Toast.makeText(BuyerLoginActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect details", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }

            }
        });
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}