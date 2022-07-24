package com.app.gogreen.activities.buyer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.app.gogreen.activities.helpers.BaseActivity;
import com.app.gogreen.databinding.ActivityPlantDetailsBinding;
import com.app.gogreen.fragments.HomeFragment;
import com.app.gogreen.models.PlantModelClass;
import com.app.gogreen.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class PlantDetailsActivity extends BaseActivity {

    LocationManager locationManager;

    LocationListener locationListener;

    public static PlantModelClass model;
    ActivityPlantDetailsBinding binding;
    String userId="";
    AlertDialog alertDialog;
    public static double lati, longi;
    public static String  quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_plant_details);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        model = HomeFragment.model;

        Picasso.with(this).load(model.getPicUrl()).placeholder(R.drawable.loading).into(binding.imgPlant);
        binding.tvPlantName.setText(model.getTitle());
        binding.tvPrice.setText("£"+model.getPrice());
        binding.tvDescription.setText(model.getDescription());

        getCurrentLocation();

        binding.imgPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , FullScreenImageActivity.class);
                intent.putExtra("URI",model.getPicUrl());
                startActivity(intent);
            }
        });
        binding.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dailogBuilder = new AlertDialog.Builder(PlantDetailsActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_layout, null);
                dailogBuilder.setView(dialogView);

                final EditText edtQuantity = dialogView.findViewById(R.id.edtQuantity);
                final Button btnSub = dialogView.findViewById(R.id.btnSub);

                btnSub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        quantity = edtQuantity.getText().toString().trim();
                        if(quantity.isEmpty()){
                            edtQuantity.setError("Required!");
                            edtQuantity.requestFocus();
                            return;
                        }
                        alertDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), PaypalPaymentActivity.class));
                    }
                });
                alertDialog = dailogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

            }
        });
    }

    private void getCurrentLocation() {
        showProgressDialog();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
               lati = location.getLatitude();
               longi = location.getLongitude();

               hideProgressDialog();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) { }
            @Override
            public void onProviderEnabled(String s) { }
            @Override
            public void onProviderDisabled(String s) { }
        };
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 100, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 100, locationListener);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 100, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 100, locationListener);
                }
            }
        }
    }
}