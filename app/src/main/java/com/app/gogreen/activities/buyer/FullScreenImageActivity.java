package com.app.gogreen.activities.buyer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.app.gogreen.R;
import com.squareup.picasso.Picasso;

//Display image in full screen.
public class FullScreenImageActivity extends AppCompatActivity {

ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        imageView = findViewById(R.id.fullImage);
        Intent intent = getIntent();
        String s = intent.getStringExtra("URI");
        Uri uri = Uri.parse(s);

        Picasso.with(FullScreenImageActivity.this).load(uri).into(imageView);
    }
}
