package com.alarm.technothumb.alarmapplication.travel_record;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alarm.technothumb.alarmapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by NIKUNJ on 12-02-2018.
 */

public class AudioRecordFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    private Location mLocation;
    MapView mapView;
    DBOpenHelper db;
    List<model> markersArray;
    LocationManager locationManager;
    double lat, lng;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_map_record, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       markersArray = new ArrayList<model>();

        db = new DBOpenHelper(getActivity());
        markersArray = db.getData();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission") Location location =locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null) {
            //     System.out.println("Provider " + provider + " has been selected.");

            onLocationChanged(location);
        } else {

        }

        return v;

    }

    private void onLocationChanged(Location location) {

        lat = location.getLatitude();
        lng = location.getLongitude();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng delhi =new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(delhi));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng),  10));
        for(int i = 0 ; i < markersArray.size() ; i++ ) {


            createMarker(markersArray.get(i).getLatitude(), markersArray.get(i).getLongitude(), markersArray.get(i).getNoteText(),markersArray.get(i).getAddress(), mMap);
        }

    }

    protected Marker createMarker(String latitude, String longitude, String noteText, String address, GoogleMap mMap) {

        Double lati = Double.valueOf(latitude);
        Double log = Double.valueOf(longitude);

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lati, log))
                .anchor(0.5f, 0.5f)
                .title(noteText)
                .snippet(address)
        );
    }

}
