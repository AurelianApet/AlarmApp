package com.alarm.technothumb.alarmapplication.travel_record;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.travel_record.entities.LocationSearchTimer;
import com.alarm.technothumb.alarmapplication.travel_record.entities.MyLocationManager;
import com.alarm.technothumb.alarmapplication.travel_record.storage.Preferences;
import com.alarm.technothumb.alarmapplication.travel_record.utils.Constants;
import com.alarm.technothumb.alarmapplication.travel_record.utils.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

/**
 * Created by NIKUNJ on 08-02-2018.
 */

public class Map_Fragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener,LocationListener {

    public Context mContext;

    private FragmentActivity mActivity;

    private MyLocationManager mLocationManager;

    private Toolbar mToolbar;

    private static FragmentManager mFragmentManager;

    private static Fragment mFragment;

    private FabSpeedDial mFabAdd;

    private FabSpeedDial mFabTypes;

    private View view;

    private GoogleMap mMap;

    private Marker currentMarker;

    private static ArrayList<Marker> mMarkers = new ArrayList<>();

    private MarkerOptions currentMarkerOptions;

    private LatLng currentMarkerLocation;

    private BitmapDescriptor myLocationIcon;

    private static LatLngBounds.Builder builder;

    String address;

    public static final int PLACE_PICKER_REQUEST = 1;

    public Map_Fragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragment = this;
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();

        mFragmentManager = getChildFragmentManager();
        mLocationManager = Util.getLocationManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpLocationUpdates();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

//        final View v = inflater.inflate(R.layout.fragment_map, container, false);

        getActivity().setTitle("Map");
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_map, container, false);

            setViews();
            setUpMap();
        } else {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }

        return view;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        Util.onRequestPermissionsResult(mActivity, mContext, requestCode, grantResults);
        if (Util.isPermissionsGranted(Constants.LOCATION_PERMISSION_CODES, mContext)) {
            // TODO: may we could have a NPE here. Check it deep in the future.
            mLocationManager.startLocationUpdatesByPrecisionStatus();
            new GetCurrentCoordinates().execute();

        } else {
            mActivity.finish();
        }
    }

    @Override
    public void onDestroyView() {
        if (mLocationManager != null) {
            mLocationManager.stopUpdates(false);
        }

        super.onDestroyView();
    }

    private void setUpLocationUpdates() {
        if (mLocationManager != null) {
            mLocationManager.startLocationUpdatesByPrecisionStatus();
            Location lastKnowLoc = mLocationManager.getLastKnownLocation();
            setUpCurrentMarkerLocation(lastKnowLoc);
        } else {
            Util.initGPSManager(mContext, mActivity);
            mLocationManager = Util.getLocationManager();
            mLocationManager.startLocationUpdatesByPrecisionStatus();
            Location lastKnowLoc = mLocationManager.getLastKnownLocation();
            setUpCurrentMarkerLocation(lastKnowLoc);
        }
    }

    private void setUpCurrentMarkerLocation(Location lastKnowLoc) {
        if (MyLocationManager.isLocationInvalid(lastKnowLoc)) {
            new GetCurrentCoordinates().execute();
        } else {
            currentMarkerLocation = new LatLng(lastKnowLoc.getLatitude(), lastKnowLoc.getLongitude());

            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

            List<Address> addresses  = null;
            try {
                addresses = geocoder.getFromLocation(lastKnowLoc.getLatitude(),lastKnowLoc.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
//             address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()



//            String a = address;
//            + city + state + zip +country;

//            Log.e("adrs",a);
        }
    }

    private void setViews() {
        mFabAdd = (FabSpeedDial) view.findViewById(R.id.fab_map_add_options);
        mFabAdd.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                String latitude = String.valueOf(currentMarkerLocation.latitude);
                String longitude = String.valueOf(currentMarkerLocation.longitude);

                if (itemId == R.id.ic_audio_record){

                    Intent i_record = new Intent(getActivity(),AudioNote_RecordActivity.class);
                    i_record.putExtra("latitude",latitude);
                    i_record.putExtra("longitude",longitude);
                    startActivity(i_record);
                    getActivity().finish();
                }

              if (itemId == R.id.ic_camera) {

                  Intent i_camera =new Intent(getActivity(),CameraActivity.class);
                  i_camera.putExtra("latitude",latitude);
                  i_camera.putExtra("longitude",longitude);
                  startActivity(i_camera);
//                  getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                  getActivity().finish();


                    return true;

                } else if (itemId == R.id.ic_goto_my_position) {

                    if (!Util.isPermissionsGranted(Constants.LOCATION_PERMISSION_CODES, mContext)) {
                        Util.checkPermissions(mActivity, Constants.LOCATION_PERMISSIONS_FLAG,
                                Constants.LOCATION_PERMISSION_CODES,
                                Constants.LOCATION_PERMISSIONS_CODE, mFragment);
                    } else {
                        new GetCurrentCoordinates().execute();
                    }
                    return true;
                }
                return false;
            }
        });

        mFabTypes = (FabSpeedDial) view.findViewById(R.id.fab_map_type_options);
        mFabTypes.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                int map_type;

                if (itemId == R.id.ic_change_map_to_normal) {
                    map_type = GoogleMap.MAP_TYPE_NORMAL;

                } else if (itemId == R.id.ic_change_map_to_terrain) {
                    map_type = GoogleMap.MAP_TYPE_TERRAIN;

                } else {
                    map_type = GoogleMap.MAP_TYPE_SATELLITE;
                }

                mMap.setMapType(map_type);
                return true;
            }
        });

    }

    private void setUpMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) mFragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void updateMapPosition() {
        if (currentMarker != null) {
            currentMarker.remove();
        }

        Preferences.setUserLatitudePos(mContext, currentMarkerLocation.latitude);
        Preferences.setUserLongitudePos(mContext, currentMarkerLocation.longitude);

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(currentMarkerLocation.latitude, currentMarkerLocation.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        address = addresses.get(0).getAddressLine(0);

        currentMarkerOptions = new MarkerOptions()
                .position(currentMarkerLocation)
                .title(address)
                .icon(myLocationIcon);
        currentMarker = mMap.addMarker(currentMarkerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentMarkerLocation, Constants.DEFAULT_MAP_ZOOM));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myLocationIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location);

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMapClickListener(this);

        if (currentMarkerLocation != null) {
            builder = new LatLngBounds.Builder();
            builder.include(currentMarkerLocation);

            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(currentMarkerLocation.latitude, currentMarkerLocation.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            address = addresses.get(0).getAddressLine(0);
            // TODO: change it to get live GPS position instead
            currentMarkerOptions = new MarkerOptions()
                    .position(currentMarkerLocation)
                    .title(address)
                    .icon(myLocationIcon);
            currentMarker = mMap.addMarker(currentMarkerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentMarkerLocation, Constants.DEFAULT_MAP_ZOOM));

//            loadDatabaseLocationsOnMap();
        }

    }

    @Override
    public void onMapClick(LatLng point) {
        currentMarkerLocation = point;
        updateMapPosition();
    }







    public void openLocationConnectionTimeout() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mActivity)
                .title(R.string.notice)
                .content(Util.getString(mContext, R.string.location_search_timeout))
                .positiveColorRes(R.color.colorPrimaryDark)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Location betterLocation = mLocationManager
                                .getBetterLocation();
                        if (MyLocationManager.isLocationInvalid(betterLocation)) {
                            Util.openAlertDialog(mActivity, Util.getString(mContext, R.string.invalid_position), false);
                            dialog.dismiss();
                        } else {
                            currentMarkerLocation = new LatLng(betterLocation.getLatitude(), betterLocation.getLongitude());
                            updateMapPosition();
                        }
                    }
                })
                .negativeColorRes(R.color.colorPrimaryDark)
                .negativeText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mLocationManager.setTimerState(false);
                        dialog.dismiss();
                    }
                });

        MaterialDialog alertDialog = builder.build();
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        mMap.getMaxZoomLevel();

        // Setting latitude and longitude in the TextView tv_location
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    private class GetCurrentCoordinates extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;

        private final String progressMessage = Util.getString(mContext, R.string.getting_current_location);

        private LocationSearchTimer mTimeout;

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setCancelable(true);
            progressDialog.setMessage(progressMessage);
            progressDialog.show();
            mTimeout = new LocationSearchTimer(mContext, this, progressDialog,
                    progressMessage, MyLocationManager.TIMER_TIMEOUT,
                    MyLocationManager.TIMER_TIMEOUT_INTERVAL, true);
            mTimeout.start();
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Void doInBackground(final Void... params) {

            if (mLocationManager.isGPSProviderEnable()) {
                Log.e("CAPTURE", "BY BOTH");
                while (MyLocationManager.isLocationInvalid(
                        mLocationManager.getSatelliteLocation())
                        && !mLocationManager.getTimerState() && !isCancelled()) {
//                    Log.i("Location", "SEARCHING LOCATION");
                }
            } else {
                Log.e("CAPTURE", "BY NETWORK");
                while (MyLocationManager
                        .isLocationInvalid(mLocationManager.getNetworkLocation())
                        && !mLocationManager.getTimerState() && !isCancelled()) {
//                    Log.i("Location", "SEARCHING LOCATION");
                }
            }
            return null;
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(final Void result) {
            super.onPostExecute(result);

            try {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (Exception e) {
                Log.e("Map", e.getMessage());
            }

            mTimeout.cancel();

            Location betterLocation = mLocationManager.getBetterLocation();

            if (MyLocationManager
                    .isLocationInvalid(mLocationManager.getSatelliteLocation())) {
                if (MyLocationManager
                        .isLocationInvalid(mLocationManager.getNetworkLocation())) {
                    Location lastKnowLoc = mLocationManager.getLastKnownLocation();
                    if (MyLocationManager
                            .isLocationInvalid(lastKnowLoc)) {
                        mActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                openLocationConnectionTimeout();
                            }
                        });
                    } else {
                        currentMarkerLocation = new LatLng(lastKnowLoc.getLatitude(), lastKnowLoc.getLongitude());
                        updateMapPosition();
                    }

                } else {
                    currentMarkerLocation = new LatLng(betterLocation.getLatitude(), betterLocation.getLongitude());
                    updateMapPosition();
                }
            } else {
                Location satteliteLocation = mLocationManager.getSatelliteLocation();
                currentMarkerLocation = new LatLng(satteliteLocation.getLatitude(), satteliteLocation.getLongitude());
                updateMapPosition();
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onCancelled()
         */
        @Override
        protected void onCancelled() {
            mTimeout.cancel();
            mLocationManager.setTimerState(false);

            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                Log.e("Map", e.getMessage());
            }

            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    openLocationConnectionTimeout();
                }
            });
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onCancelled(java.lang.Object)
         */
        @Override
        protected void onCancelled(final Void result) {
            mTimeout.cancel();
            mLocationManager.setTimerState(false);
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    openLocationConnectionTimeout();
                }
            });
        }
    }

}
