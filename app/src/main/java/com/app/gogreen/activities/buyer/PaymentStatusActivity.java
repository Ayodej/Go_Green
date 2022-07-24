package com.app.gogreen.activities.buyer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.databinding.DataBindingUtil;

import com.app.gogreen.activities.helpers.BaseActivity;
import com.app.gogreen.databinding.ActivityPaymentStatusBinding;
import com.app.gogreen.models.PlantModelClass;
import com.app.gogreen.models.OrderModelClass;
import com.app.gogreen.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentStatusActivity extends BaseActivity {

    ActivityPaymentStatusBinding binding;

    DatabaseReference dbRef;
    String userId="";
    PlantModelClass plantModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_status);

        dbRef = FirebaseDatabase.getInstance().getReference("PlantOrders");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        plantModel = PlantDetailsActivity.model;

        Intent intent = getIntent();
        try {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("details"));
            showDetails(jsonObject.getJSONObject("response"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDetails(JSONObject response) {
        try {
            binding.txtId.setText("Transaction ID: "+response.getString("id"));
            String resp = response.getString("state");
            if(resp.equals("approved")){

                String id = dbRef.push().getKey();
                OrderModelClass model = new OrderModelClass(id,plantModel.getId(),plantModel.getTitle(),plantModel.getPrice(),userId, MainActivity.userName,
                        PlantDetailsActivity.lati, PlantDetailsActivity.longi, MainActivity.address,
                        PlantDetailsActivity.quantity,"pending");

                dbRef.child(id).setValue(model);
                binding.txtStatus.setText("Order is placed successfully.");

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                },2000);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}