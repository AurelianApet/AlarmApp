package com.alarm.technothumb.alarmapplication.travel_record;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.alarm.technothumb.alarmapplication.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    private Location mLocation;
    Double latitude;
    Double longitude;
    private String noteFilter;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        gpsTracker = new GPSTracker(getApplicationContext());
        mLocation = gpsTracker.getLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        img = findViewById(R.id.img);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);
        noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();

        Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
        cursor.moveToFirst();

//
//        // Add a marker in Sydney and move the camera
        latitude = Double.valueOf(cursor.getString(cursor.getColumnIndex(DBOpenHelper.Latitude)));
        longitude = Double.valueOf(cursor.getString(cursor.getColumnIndex(DBOpenHelper.Longitude)));
        String photoPath = cursor.getString(cursor.getColumnIndex(DBOpenHelper.Photopath));

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address1 = addresses.get(0).getAddressLine(0);

        loadPhoto(photoPath);


//
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title(address1));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

    public void loadPhoto(String photopath) {

        if (photopath != null)
        {
            Bitmap bitmap = BitmapFactory.decodeFile(photopath);
            Bitmap bitmapReduced = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
            img.setImageBitmap(bitmapReduced);
            img.setScaleType(ImageView.ScaleType.FIT_XY);
            img.setTag(photopath);

        }
    }
}
