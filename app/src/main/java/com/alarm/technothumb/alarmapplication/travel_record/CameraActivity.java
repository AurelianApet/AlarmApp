package com.alarm.technothumb.alarmapplication.travel_record;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alarm.technothumb.alarmapplication.R;

import com.alarm.technothumb.alarmapplication.anniversaries.Anniversary;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String action;
    private EditText editor;
    private ImageView photoField;
    //private EditText location;
    private String noteFilter;
    private String oldText, oldPath;
    private String photopath;
    public static final int CAMERA_CODE = 567,GALLERY_CODE = 456;
    public static int PLACE_PICKER_REQUEST = 1;
    public static int PLACE_PICKER_EDIT = 2;
    public static Double lat, log;
    private GPSTracker gpsTracker;
    private Location mLocation;
    String latitude, longitude,country;
    String currentDateTime;
    int TravellingNoteId;
    Boolean googleMap = false;
//    String latitude1,longitude1;
    String newText,String ,newPhoto,newPhoto1;
    private boolean edit = false;
    String address2;
    Double latilong,logilong;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

       // setTitle("Edit Record");

        currentDateTime = DateFormat.getDateTimeInstance().format(new Date());



        editor = (EditText) findViewById(R.id.editText);
        photoField = (ImageView) findViewById(R.id.imageView1);
        //location = (EditText) findViewById(R.id.editText);
        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);
        edit = intent.getBooleanExtra("edit", false);

        Log.e("edit", edit+"");

        if(edit){
            update(intent);
        }

        String action1 = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action1) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }else {

            }
        }

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle("Traveling Entry ");
        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            oldPath = cursor.getString(cursor.getColumnIndex(DBOpenHelper.Photopath));
            editor.setText(oldText);
//            editor.requestFocus();
            //Toast.makeText(this,oldPath, Toast.LENGTH_LONG).show();
            loadPhoto(oldPath);

        }


//        Button photoButton = (Button) findViewById(R.id.fromCamera);
        photoField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };



                AlertDialog.Builder builder1 = new AlertDialog.Builder(CameraActivity.this);

                builder1.setTitle("Add Photo!");

                builder1.setItems(options, new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int item) {

                        if (options[item].equals("Take Photo"))

                        {

                            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            photopath = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
                            File photofile = new File(photopath);
                            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photofile));
                            startActivityForResult(intentCamera, CAMERA_CODE);

/*
                            CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .start(this);
*/

//                            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            photopath = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
//                            File photofile = new File(photopath);
//                            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photofile));
//                            startActivityForResult(intentCamera, CAMERA_CODE);


                        }

                        else if (options[item].equals("Choose from Gallery"))

                        {

                            Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            startActivityForResult(intent, GALLERY_CODE);



                        }

                        else if (options[item].equals("Cancel")) {

                            dialog.dismiss();

                        }

                    }

                });

                builder1.show();

            }
        });


    }

    private void update(Intent intent) {

        String Path = intent.getStringExtra("imagePath");
        String Title = intent.getStringExtra("title");
        TravellingNoteId = intent.getIntExtra("id", 0);
        Uri imageFilePath = Uri.parse(Path);

        photoField.setImageURI(imageFilePath);
        editor.setText(Title);

        Log.e("CameraId", TravellingNoteId+"");

    }

    private void handleSendText(Intent intent) {

        googleMap = true;
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (sharedText != null) {
        String[] items=sharedText.split("\n");
        String placeName = items[0];
       String   Addresss1 = items[1];

            String[] GoogleAddress=Addresss1.split("\n");
           address2 = GoogleAddress[0];

       Log.e("address",address2 );

            editor.setText(placeName);

            Geocoder coder = new Geocoder(this);
            List<Address> address;

            try {
                // May throw an IOException
                address = coder.getFromLocationName(address2, 5);
                if (address == null) {

                }else {

                    Address location = address.get(0);
                    latilong = location.getLatitude();
                    logilong = location.getLongitude();



                }
            } catch (IOException ex) {

                ex.printStackTrace();
            }

         //   Log.e("sharedText", sharedText);
            // Update UI to reflect text being shared
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //menu.getItem(1).setEnabled(false);
      /*  if (action.equals(Intent.ACTION_INSERT)) {
            getMenuInflater().inflate(R.menu.menu_travel_editor, menu);
        }*/
        if(edit){
            getMenuInflater().inflate(R.menu.menu_travel_main, menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_travel_editor, menu);
        }
      /*  if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_travel_editor, menu);
        }*/
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Intent mIntent = new Intent(this, TravelRecordActivity.class);
                //setResult(RESULT_OK, mIntent);
                startActivity(mIntent);
//                CameraActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//                finishEditing();
                break;
            case R.id.action_delete_all:
                deleteNote();
                break;
            case R.id.action_save:
                // MenuItem menuItem = (MenuItem) findViewById(R.id.action_save);

                if(edit){
                    if (editor.getText().toString().equals(""))
                    {
                        Toast.makeText(CameraActivity.this, "Enter Title", Toast.LENGTH_SHORT).show();
                    }else {

                        newText = editor.getText().toString();
                        newPhoto = photoField.getDrawable().toString();

                        switch (action) {
                            case Intent.ACTION_INSERT:
                                if (newText.length() == 0) {
                                    setResult(RESULT_CANCELED);
                                }
                                else if (newPhoto.length() == 0){
                                    setResult(RESULT_CANCELED);
                                }
                                else {

                                 //   newPhoto1 = photoField.getTag().toString();

                                    if (!isNetworkAvailable()) {
                                        // Create an Alert Dialog
                                        AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
                                        // Set the Alert Dialog Message
                                        builder.setMessage("Internet Connection Required for Map")
                                                .setCancelable(false)
                                                .setPositiveButton("Retry",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog,
                                                                                int id) {
                                                                Intent intent = new Intent(CameraActivity.this, CameraActivity.class);
                                                                startActivity(intent);
                                                                dialog.cancel();
                                                            }
                                                        })
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                        dialog.cancel();
                                                        // System.exit(0);
                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    } else {


                                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                                        Intent intent;
                                        try {
                                            intent = builder.build(CameraActivity.this);
                                            startActivityForResult(intent, PLACE_PICKER_EDIT );
                                        } catch (GooglePlayServicesRepairableException e) {
                                            e.printStackTrace();
                                        } catch (GooglePlayServicesNotAvailableException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }

                                }

                }else {

                if (editor.getText().toString().equals(""))
                {
                    Toast.makeText(CameraActivity.this, "Enter Title", Toast.LENGTH_SHORT).show();
                }
                else if (photoField.getDrawable() == null){
                    Toast.makeText(CameraActivity.this, "Upload Image", Toast.LENGTH_SHORT).show();
                }
                else if (photoField.getTag() == null)
                {
                    Toast.makeText(CameraActivity.this, "Upload Image", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(!googleMap){
                        newText = editor.getText().toString();
                     newPhoto = photoField.getDrawable().toString();

                    switch (action) {
                        case Intent.ACTION_INSERT:
                            if (newText.length() == 0) {
                                setResult(RESULT_CANCELED);
                            }
                            else if (newPhoto.length() == 0){
                                setResult(RESULT_CANCELED);
                            }
                            else {

                                 newPhoto1 = photoField.getTag().toString();

                                if (!isNetworkAvailable()) {
                                    // Create an Alert Dialog
                                    AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
                                    // Set the Alert Dialog Message
                                    builder.setMessage("Internet Connection Required for Map")
                                            .setCancelable(false)
                                            .setPositiveButton("Retry",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,
                                                                            int id) {
                                                            Intent intent = new Intent(CameraActivity.this, CameraActivity.class);
                                                            startActivity(intent);
                                                            dialog.cancel();
                                                        }
                                                    })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {

                                                    dialog.cancel();
                                                    // System.exit(0);
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }else {


                                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                                    Intent intent;
                                    try {
                                        intent = builder.build(CameraActivity.this);
                                        startActivityForResult(intent, PLACE_PICKER_REQUEST);
                                    } catch (GooglePlayServicesRepairableException e) {
                                        e.printStackTrace();
                                    } catch (GooglePlayServicesNotAvailableException e) {
                                        e.printStackTrace();
                                    }

                                }


                             //   insertNote(newText, newPhoto1, latitude, longitude, currentDateTime);

                               /* Intent i =new Intent(CameraActivity.this,TravelRecordActivity.class);
                                i.putExtra("title", newText);
                                i.putExtra("Photo", newPhoto1);
                                i.putExtra("currentTime", currentDateTime);
                                startActivity(i);*/
                            }
                            break;
                        case Intent.ACTION_EDIT:
                            if (newText.length() == 0) {
                                deleteNote();
                            } else if (oldText.equals(newText) && oldPath.equals(newPhoto)) {
                                setResult(RESULT_CANCELED);
                            } else {
                                newPhoto1 = photoField.getTag().toString();
                                String AudioFile = "";

                            //    updateNote(newText, newPhoto1, latitude, longitude,currentDateTime, address, AudioFile, id );
                              //  insertNote(newText, newPhoto1, lat, log, currentDateTime, address, AudioFile, Type);
                                Intent i =new Intent(CameraActivity.this,TravelRecordActivity.class);
                                startActivity(i);
                            }

                    }} else {

                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(this, Locale.getDefault());

                        String address = null, city = null, state = null, country1 = null;


                        try {
                            addresses = geocoder.getFromLocation(latilong, logilong, 1);
                         address = addresses.get(0).getAddressLine(0);
                         city = addresses.get(0).getLocality();
                         state = addresses.get(0).getAdminArea();
                            country1 = addresses.get(0).getCountryName();

                            if((city+"").equals("null")){
                                city = "Unavailable";
                            }else {
                                city = addresses.get(0).getLocality();
                            }

                            if((state+"").equals("null")){
                                state = "Unavailable";
                            }else {
                                state = addresses.get(0).getAdminArea();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }








                        String AudioFile = " ";
                        int Type = 1;
                        newText = editor.getText().toString();
                        newPhoto = photoField.getTag().toString();



                        insertNote(newText, newPhoto, latilong, logilong, currentDateTime, address,city,state, country1, AudioFile, Type);

                        Intent i =new Intent(CameraActivity.this,TravelRecordActivity.class);
                        startActivity(i);



                    }
                }
//                    finishEditing();
                }

                break;

        }


        return true;
    }

    private void deleteNote() {
        DBOpenHelper db;
        db = new DBOpenHelper(this);
        db.isdelect(TravellingNoteId);

        Intent intent = new Intent(CameraActivity.this, TravelRecordActivity.class);
        startActivity(intent);


    }

    private void updateNote(String newText, String newPhoto1, Double lat, Double log, String currentDateTime, String address,String City, String State, String Country, String audioFile, int type, int travellingNoteId) {


        DBOpenHelper db;
        db = new DBOpenHelper(this);
        db.updateData(newText, newPhoto1, lat, log, currentDateTime, address, City,  State, Country, audioFile, type, travellingNoteId);
    }




    private void insertNote(String noteText, String photoPath, Double latitude, Double longitude, String currentDateTime, String address, String City, String State, String Country, String audioFile, int type) {
        DBOpenHelper db;
        db = new DBOpenHelper(this);
        db.insertData(noteText, photoPath, latitude, longitude, currentDateTime, address, City,  State, Country, audioFile, type);

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();


    }


    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK){

            if (requestCode == CAMERA_CODE){
                // helper.loadPhoto(photopath);
           //    loadPhoto(photopath);
                File imagePhoto = new File(photopath);

                CropImage.activity(Uri.fromFile(imagePhoto))
                        .start(this);



            }
            else if (requestCode == GALLERY_CODE) {
                Uri selectedImageUri = data.getData();
                String picturePath = getPath( this.getApplicationContext(), selectedImageUri );
//                Toast.makeText(this,picturePath,Toast.LENGTH_LONG).show();
                File imagePhoto = new File(picturePath);
                CropImage.activity(Uri.fromFile(imagePhoto))
                        .start(this);

             //   loadPhoto(picturePath);
            }

           else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    BitmapFactory.Options options = new BitmapFactory.Options();

                /*    BitmapFactory.decodeFile(java.lang.String.valueOf(resultUri), options);
                    options.inJustDecodeBounds = true;
                  //  BitmapFactory.decodeFile(photopath, options);
                    // Calculate inSampleSize
                    options.inSampleSize = calculateInSampleSize(options, 768, 1280);
                    // Decode bitmap with inSampleSize set
                 //   options.inJustDecodeBounds = false;

                    final Bitmap bitmap = BitmapFactory.decodeFile(java.lang.String.valueOf(resultUri),
                            options);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);*/
                   // photoField.setImageBitmap(bitmap);
                    photoField.setImageURI(resultUri);
                    photoField.setTag(resultUri);



                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
                if(resultCode==RESULT_OK){
                    if(requestCode== PLACE_PICKER_REQUEST){
                    Place place = PlacePicker.getPlace(data,this);


                    String address = String.format("%s, ", place.getName());

                    lat = place.getLatLng().latitude;
                    log = place.getLatLng().longitude;


                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(this, Locale.getDefault());
                        String city = "Unavailable", state="Unavailable",country = null;


                        try {
                            addresses = geocoder.getFromLocation(lat, log, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                           city = addresses.get(0).getLocality();
                            state = addresses.get(0).getAdminArea();
                           country = addresses.get(0).getCountryName();

                           Log.e("city", city+"");
                            if((city+"").equals("null")){
                                city = "Unavailable";
                            }else {
                                city = addresses.get(0).getLocality();
                            }

                            if((state+"").equals("null")){
                                state = "Unavailable";
                            }else {
                                state = addresses.get(0).getAdminArea();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }




                    android.util.Log.e("Lat", lat+"|");
                    android.util.Log.e("Log", log+"");
                    String AudioFile = "";
                    int Type = 1;



                    insertNote(newText, newPhoto1, lat, log, currentDateTime, address,city,state, country, AudioFile, Type);

                    Intent i =new Intent(CameraActivity.this,TravelRecordActivity.class);
                    startActivity(i);



                }

                 else  if(requestCode== PLACE_PICKER_EDIT){
                        Place place = PlacePicker.getPlace(data,this);


                        String address = String.format("%s, ", place.getName());
                        lat = place.getLatLng().latitude;
                        log = place.getLatLng().longitude;

                        android.util.Log.e("Lat", lat+"|");
                        android.util.Log.e("Log", log+"");
                        String AudioFile = "";
                        int Type = 1;

                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(this, Locale.getDefault());
                        String city = "null", state="null",country = "null";


                        try {
                            addresses = geocoder.getFromLocation(lat, log, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            city = addresses.get(0).getLocality();
                            state = addresses.get(0).getAdminArea();
                            country = addresses.get(0).getCountryName();

                            if(city.equals("null")){
                                city = "Unavailable";
                            }else {
                                city = addresses.get(0).getLocality();
                            }

                            if(state.equals("null")){
                                city = "Unavailable";
                            }else {
                                state = addresses.get(0).getAdminArea();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        updateNote(newText, newPhoto1, lat, log, currentDateTime, address,city,state, country, AudioFile, Type, TravellingNoteId);

                        Intent i =new Intent(CameraActivity.this,TravelRecordActivity.class);
                        startActivity(i);



                    }
            }



        }

    }


    public void loadPhoto(String photopath) {

        if (photopath != null)
        {
           /* Bitmap bitmap = BitmapFactory.decodeFile(photopath);
//            Toast.makeText(this,"1",Toast.LENGTH_LONG).show();
            Bitmap bitmapReduced = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
            photoField.setImageBitmap(bitmapReduced);
            //photoField.setScaleType(ImageView.ScaleType.FIT_XY);
            photoField.setTag(photopath);*/


            BitmapFactory.Options options = new BitmapFactory.Options();

            BitmapFactory.decodeFile(photopath, options);
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(photopath, options);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 768, 1280);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            final Bitmap bitmap = BitmapFactory.decodeFile(photopath,
                    options);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            photoField.setImageBitmap(bitmap);
            photoField.setTag(photopath);
        }
     //   getLocation();
    }



    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

    private void getLocation() {
        gpsTracker = new GPSTracker(getApplicationContext());
        mLocation = gpsTracker.getLocation();
        latitude = String.valueOf(mLocation.getLatitude());
        longitude = String.valueOf(mLocation.getLongitude());

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses.size() > 0) {
            country = addresses.get(0).getCountryName();
            android.util.Log.e("country",country);

        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
