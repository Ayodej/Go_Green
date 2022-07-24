package com.app.gogreen.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.gogreen.R;
import com.app.gogreen.activities.buyer.ForgotPasswordActivity;
import com.app.gogreen.activities.buyer.MainActivity;
import com.app.gogreen.models.UserModelClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewProfileFragment extends Fragment {
    Context context;
    View view;
    EditText edtName, edtAddress, edtEmail;
    TextView tvEmailStatus;
    ImageView imgEdit;
    Button btnUpdate,  btnUpdatePass;
    String userId, userName, userAddress, email, password;
    DatabaseReference databaseReference;
    int count = 0;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_profile, container, false);

        context = container.getContext();

        databaseReference = FirebaseDatabase.getInstance().getReference("UsersData");

        imgEdit = view.findViewById(R.id.imgEdit);
        edtName = view.findViewById(R.id.edtName);
        edtAddress = view.findViewById(R.id.edtAddress);
        edtEmail = view.findViewById(R.id.edtEmail);
        tvEmailStatus = view.findViewById(R.id.tvEmailStatus);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnUpdatePass = view.findViewById(R.id.btnUpdatePass);

        userId  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userName = MainActivity.userName;
        userAddress = MainActivity.address;
        email = MainActivity.email;
        password = MainActivity.password;

        edtName.setText(userName);
        edtAddress.setText(userAddress);
        edtEmail.setText(email);
        edtName.setEnabled(false);
        edtAddress.setEnabled(false);
        edtEmail.setEnabled(false);

        tvEmailStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkEmailIsVerified()){
                    sendVerificationEmail();
                }
            }
        });

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if(count%2 != 0) {
                    edtName.setEnabled(true);
                    edtName.requestFocus();
                    edtAddress.setEnabled(true);
                }else {
                    edtName.setEnabled(false);
                    edtAddress.setEnabled(false);
                }
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = edtName.getText().toString().trim();
                userAddress = edtAddress.getText().toString().trim();

                //Validations to all data fileds..
                if(TextUtils.isEmpty(userName)){
                    edtName.setError("Required!");
                    edtName.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(userAddress)){
                    edtAddress.setError("Required!");
                    edtAddress.requestFocus();
                    return;
                }

                editAccount();
            }
        });
        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ForgotPasswordActivity.class));

            }
        });
        return view;
    }

    private void editAccount() {
        final UserModelClass user = new UserModelClass(userId,email,password,userName,userAddress);
        databaseReference.child(userId).setValue(user);
        edtName.setText(userName);
        edtAddress.setText(userAddress);
        edtName.setEnabled(false);
        edtAddress.setEnabled(false);

        Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStart() {
        super.onStart();

        if(!checkEmailIsVerified()){
            tvEmailStatus.setText("Email is not Verified, CLick to verify");
        }else {
            tvEmailStatus.setText("Email is Verified");
        }
    }

    private boolean checkEmailIsVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.isEmailVerified()){
            return true;
        }else {
            return false;
        }
    }

    private void sendVerificationEmail() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Verification email sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
