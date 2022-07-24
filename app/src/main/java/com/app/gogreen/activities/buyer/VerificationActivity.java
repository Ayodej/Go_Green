package com.app.gogreen.activities.buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.gogreen.R;
import com.app.gogreen.databinding.ActivityVerificationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationActivity extends AppCompatActivity {

    ActivityVerificationBinding binding;
    FirebaseAuth mAuth;
    public static boolean emailVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verification);

        mAuth = FirebaseAuth.getInstance();

        binding.tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Verification email sent to " + firebaseUser.getEmail(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        binding.btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInAndVerifyEmail();
            }
        });
    }
    private void SignInAndVerifyEmail() {
        if(checkEmailVerified()) {
            emailVerified = true;
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        } else {
            emailVerified = false;
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        }
    }
    private boolean checkEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.isEmailVerified()){
            return true;
        }else {
            return false;
        }
    }

}
