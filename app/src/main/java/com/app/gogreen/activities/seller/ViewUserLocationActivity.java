package com.app.gogreen.activities.seller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.app.gogreen.R;
import com.app.gogreen.activities.seller.ViewBuyerOrdersActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewUserLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        LatLng userLocation = new LatLng(ViewBuyerOrdersActivity.model.getBuyerLati(), ViewBuyerOrdersActivity.model.getBuyerLongi());
        mMap.addMarker(new MarkerOptions().position(userLocation).title("User Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,10));

        mMap.setOnMarkerClickListener(this);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Uri uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination="+ ViewBuyerOrdersActivity.model.getBuyerLati()+","+ ViewBuyerOrdersActivity.model.getBuyerLongi()+"&travelmode=driving");
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
        return false;
    }
}

