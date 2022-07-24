package com.app.gogreen.activities.buyer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.app.gogreen.R;
import com.app.gogreen.databinding.ActivityRegistrationBinding;
import com.app.gogreen.models.UserModelClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    ActivityRegistrationBinding binding;
    DatabaseReference databaseReference;
    String name, address;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);

        databaseReference = FirebaseDatabase.getInstance().getReference("UsersData");
        mAuth = FirebaseAuth.getInstance();

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = binding.edtName.getText().toString().trim();
                address = binding.edtAddress.getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                    binding.edtName.setError("Required!");
                    binding.edtName.requestFocus();
                   return;
                }
                if(TextUtils.isEmpty(address)){
                    binding.edtAddress.setError("Required!");
                    binding.edtAddress.requestFocus();
                    return;
                }

                saveDetails();
            }
        });
    }

    private void saveDetails(){

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        String userId = firebaseUser.getUid();
        UserModelClass user = new UserModelClass(userId, AuthActivity.email, AuthActivity.password,name,address);
        databaseReference.child(userId).setValue(user);

        Toast.makeText(this, "Your details saved", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }
}
