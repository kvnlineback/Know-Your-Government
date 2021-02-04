package com.example.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

public class PhotoDetailActivity extends AppCompatActivity {
    private Official official;
    private ImageView photoPhoto;
    private TextView photoAddress;
    private ImageView photoPartyLogo;
    private TextView photoName;
    private TextView photoTitle;
    private ConstraintLayout layout;
    private ConnectivityManager cm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        photoPhoto = findViewById(R.id.photoPhoto);
        photoAddress = findViewById(R.id.photoAddress);
        photoPartyLogo = findViewById(R.id.photoPartyLogo);
        photoName = findViewById(R.id.photoName);
        photoTitle = findViewById(R.id.photoTitle);
        layout = findViewById(R.id.photoDetailLayout);
        Intent intent = getIntent();
        if (intent.hasExtra("official")) {
            official = (Official) intent.getSerializableExtra("official");
            if (official.getParty().contains("Demo")) {
                layout.setBackgroundColor(Color.BLUE);
                photoPartyLogo.setImageResource(R.drawable.dem_logo);

            } else if (official.getParty().contains("Repub")) {
                layout.setBackgroundColor(Color.RED);
                photoPartyLogo.setImageResource(R.drawable.rep_logo);
            } else {
                layout.setBackgroundColor(Color.BLACK);
                photoPartyLogo.setVisibility(View.GONE);
            }
            photoName.setText(official.getName());
            photoTitle.setText(official.getTitle());
            photoAddress.setText(String.format(Locale.getDefault(), "%s, %s %s", OfficialDownloader.city, OfficialDownloader.state, OfficialDownloader.zip));
            if (checkConnection()) {
                Picasso picasso = new Picasso.Builder(this).build();
                picasso.load(official.getPhotoURL()).error(R.drawable.brokenimage).placeholder(R.drawable.placeholder).into(photoPhoto);
            } else
                photoPhoto.setImageResource(R.drawable.brokenimage);

        }
    }

    public void logoClick(View v) {
        if (official.getParty().contains("Demo")) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://democrats.org"));
            startActivity(i);
        } else {
            Intent j = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gop.com"));
            startActivity(j);
        }
    }

    public boolean checkConnection() {
        if (checkConn())
            return true;
        else
            return false;
    }

    private boolean checkConn() {
        if (cm == null) {
            cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null)
                return false;
        }
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
            return true;
        else
            return false;
    }
}
