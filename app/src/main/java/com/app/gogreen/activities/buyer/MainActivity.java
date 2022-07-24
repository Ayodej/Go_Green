package com.app.gogreen.activities.buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.gogreen.activities.SelectionActivity;
import com.app.gogreen.activities.helpers.BaseActivity;
import com.app.gogreen.fragments.HomeFragment;
import com.app.gogreen.fragments.AboutUsFragment;
import com.app.gogreen.fragments.ViewProfileFragment;
import com.app.gogreen.fragments.SellerResponseFragment;
import com.app.gogreen.R;
import com.app.gogreen.models.UserModelClass;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageView imgUser;
    TextView tvEmail , tvName;
    DatabaseReference databaseReference;
    public static String userId="", userName="", email="", address="", password="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("UsersData").child(userId);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.inflateHeaderView(R.layout.nav_header_main);

        Fragment selectedFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,selectedFragment).commit();

        imgUser = view.findViewById(R.id.imgUser);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvName = view.findViewById(R.id.tvName);

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();


            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedScreen(id);

        return true;
    }

    private void displaySelectedScreen(int itemId){
        Fragment fragment = null;

        switch(itemId){
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_seller_response:
                fragment = new SellerResponseFragment();
                break;
            case R.id.nav_view_profile:
                fragment = new ViewProfileFragment();
                break;
            case R.id.nav_signout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), SelectionActivity.class));
                finish();
                break;
            case R.id.nav_about_us:
                fragment = new AboutUsFragment();
                break;
        }

        if(fragment!=null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onStart() {
        super.onStart();

        showProgressDialog();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModelClass model = snapshot.getValue(UserModelClass.class);
                userName = model.getUserName();
                email = model.getEmail();
                address = model.getAddress();
                password = model.getPassword();

                for(DataSnapshot snapshot1 : snapshot.getChildren()){ }

                tvName.setText(model.getUserName());
                tvEmail.setText(model.getEmail());

                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgressDialog();
            }
        });
    }
}