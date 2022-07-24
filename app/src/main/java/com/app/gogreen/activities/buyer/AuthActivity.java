package com.app.gogreen.activities.buyer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.app.gogreen.R;
import com.app.gogreen.activities.helpers.BaseActivity;
import com.app.gogreen.databinding.ActivityAuthBinding;
import com.app.gogreen.models.UserModelClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthActivity extends BaseActivity {
    ActivityAuthBinding binding;
    private FirebaseAuth mAuth;

    public static String email, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = binding.edtEmail.getText().toString().trim();
                password = binding.editPassword.getText().toString().trim();

                if(email.isEmpty()){
                    binding.edtEmail.setError("Required!");

                    binding.edtEmail.requestFocus();
                    return;
                }
                if(password.isEmpty()){
                    binding.editPassword.setError("Required");
                    binding.editPassword.requestFocus();
                    return;
                }
                if(!password.equals(binding.edtConfirmPassword.getText().toString().trim())){
                    binding.edtConfirmPassword.setError("Password not matched!");
                    binding.edtConfirmPassword.requestFocus();
                    return;
                }
                createUserAccount(email, password);
            }
        });
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void createUserAccount(final String email, final String password) {
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    sendVerificationEmail();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
                hideProgressDialog();
            }
        });
    }

    private void sendVerificationEmail() {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    saveDataToFirebase();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveDataToFirebase() {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userId = firebaseUser.getUid();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UsersData");
                    UserModelClass user = new UserModelClass(userId,email,password,"","");
                    databaseReference.child(userId).setValue(user);

                    Toast.makeText(getApplicationContext(), "Account created on email password and verification email sent to " + firebaseUser.getEmail(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext() , VerificationActivity.class);
                    startActivity(intent);

                }
            }
        });
    }

    private boolean validateForm() {
        if (TextUtils.isEmpty(binding.edtEmail.getText().toString())) {
            binding.edtEmail.setError("Required!");

            binding.edtEmail.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(binding.editPassword.getText().toString())) {
            binding.editPassword.setError("Required!");
            return false;
        }else {
            binding.edtEmail.setError(null);
            binding.editPassword.setError(null);
            return true;
        }
    }

}