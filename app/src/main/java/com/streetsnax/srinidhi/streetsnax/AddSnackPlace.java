package com.streetsnax.srinidhi.streetsnax;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.streetsnax.srinidhi.streetsnax.models.Snack;
import com.streetsnax.srinidhi.streetsnax.models.Snacks;
import com.streetsnax.srinidhi.streetsnax.models.Tblsnackplace;
import com.streetsnax.srinidhi.streetsnax.utilities.AppConstants;
import com.streetsnax.srinidhi.streetsnax.utilities.MultiSelectionSpinner;
import com.streetsnax.srinidhi.streetsnax.utilities.PhotoUploadConfig;
import com.streetsnax.srinidhi.streetsnax.utilities.PrefUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import dfapi.ApiException;
import dfapi.ApiInvoker;
import dfapi.BaseAsyncRequest;

public class AddSnackPlace extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback {

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100, GALLERY_SELECT_IMAGE_REGUEST_CODE = 200;
    static EditText etstarttime, etendtime;
    static String time;
    public int imgbtnid;
    ProgressDialog progressDialog;
    GoogleApiClient mGoogleApiClient;
    LatLng latlang = new LatLng(12.9667, 77.5667);//Bangalore
    MapFragment mapFragment;
    Marker marker;
    ImageButton ibaddpic1, ibaddpic2, ibaddpic3, ibaddpic4;
    EditText etlandmark;
    float SnaxPlacerating=0;
    //final variables for submit
    List<String> SnaxStringsMultiSelectionSpinner;
    String landmark,Snaxplacename,desc,starttime,endtime,placeID;
    int Availability=0;
    double latitude,longitude;
    private MultiSelectionSpinner multiSelectionSpinner;
    private int PLACE_PICKER_REQUEST = 1;
    private Toolbar mToolbar;
    private Uri fileUri; // file url to store image/video

    private static File getOutputImageFile() {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                PhotoUploadConfig.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Photo Upload", "Oops! Failed create "
                        + PhotoUploadConfig.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");


        return mediaFile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_snack);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add Snack Place");

        progressDialog = new ProgressDialog(AddSnackPlace.this,
                R.style.MyMaterialTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Retrieving data...");
        progressDialog.show();

        LoadSnackTypes();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                SnaxPlacerating = rating;

            }
        });
        etstarttime = (EditText) findViewById(R.id.etstarttime);
        etendtime = (EditText) findViewById(R.id.etendtime);

        etstarttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddSnackPlace.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etstarttime.setText(selectedHour + ":" + (selectedMinute > 9 ? selectedMinute : 0 + String.valueOf(selectedMinute)));
                    }
                }, hour, minute, DateFormat.is24HourFormat(AddSnackPlace.this));//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        etendtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddSnackPlace.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etendtime.setText(selectedHour + ":" + (selectedMinute > 10 ? selectedMinute : 0 + String.valueOf(selectedMinute)));
                    }
                }, hour, minute, DateFormat.is24HourFormat(AddSnackPlace.this));//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button btnpickplace = (Button) findViewById(R.id.btnpickplace);
        btnpickplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPlacePicker();
            }
        });

        ibaddpic1 = (ImageButton) findViewById(R.id.ibaddpic1);
        ibaddpic2 = (ImageButton) findViewById(R.id.ibaddpic2);
        ibaddpic3 = (ImageButton) findViewById(R.id.ibaddpic3);
        ibaddpic4 = (ImageButton) findViewById(R.id.ibaddpic4);

        ibaddpic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // capture picture
                imgbtnid = v.getId();
                OpenDialogBoxForCameraORGallery();

            }
        });

        ibaddpic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // capture picture
                imgbtnid = v.getId();
                OpenDialogBoxForCameraORGallery();

            }
        });

        ibaddpic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // capture picture
                imgbtnid = v.getId();
                OpenDialogBoxForCameraORGallery();

            }
        });

        ibaddpic4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // capture picture
                imgbtnid = v.getId();
                OpenDialogBoxForCameraORGallery();

            }
        });


    }

    private void LoadSnackTypes() {
        String snackResponse = PrefUtil.getString(getApplicationContext(), "tblSnackType");
        try {
            Snacks snackRecords = (Snacks) ApiInvoker.deserialize(snackResponse, "", Snacks.class);
            if (snackRecords.snackRecord.size() > 0) {
                createSpinner(snackRecords.snackRecord);
                progressDialog.dismiss();
            } else
                new GetSnackTypeTask().execute();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public void OpenDialogBoxForCameraORGallery() {

        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddSnackPlace.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    captureImage();
                } else if (items[item].equals("Choose from Library")) {
                    ChooseImageFromGallery();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    private void previewImage() {

        ImageButton ib = (ImageButton) findViewById(imgbtnid);
        ib.setEnabled(false);
        // bimatp factory
        BitmapFactory.Options options = new BitmapFactory.Options();

        // down sizing image as it throws OutOfMemory Exception for larger
        // images
        options.inSampleSize = 8;

        final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

        ib.setImageBitmap(bitmap);

    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputImageFileUri();

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        //intent.putExtra("btnid", btnid);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public void ChooseImageFromGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                GALLERY_SELECT_IMAGE_REGUEST_CODE);
    }

    public Uri getOutputImageFileUri() {
        return Uri.fromFile(getOutputImageFile());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void displayPlacePicker() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected())
            return;

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d("PlacesAPI Demo", "GooglePlayServicesRepairableException thrown");
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d("PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s selected", place.getName());
                latlang = place.getLatLng();
                placeID=place.getId();
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                onMapReady(mapFragment.getMap());

            }
        }
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                previewImage();

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "No Image Selected", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        }
        if (requestCode == GALLERY_SELECT_IMAGE_REGUEST_CODE) {

            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bitmap;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
                ImageButton ib = (ImageButton) findViewById(imgbtnid);
                ib.setEnabled(false);
                ib.setImageBitmap(bitmap);
            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "No Image Selected", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to Select an image", Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_accept:
                final CharSequence[] items = {"OK","Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AddSnackPlace.this);
                builder.setTitle("Are you sure you want to submit?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        submitTheSnackDetails();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
                builder.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void submitTheSnackDetails(){
        //final variables for submit
        SnaxStringsMultiSelectionSpinner = multiSelectionSpinner.getSelectedStrings();

        etlandmark = (EditText) findViewById(R.id.etlandmark);
      landmark = etlandmark.getText().toString();

        EditText etSnaxplacename = (EditText) findViewById(R.id.etSnackplacname);
         Snaxplacename = etSnaxplacename.getText().toString();

        EditText etdesc = (EditText) findViewById(R.id.etdesc);
         desc = etdesc.getText().toString();

        RadioGroup rgAvailability = (RadioGroup) findViewById(R.id.rgAvailability);
        RadioButton rbselected = (RadioButton) findViewById(rgAvailability.getCheckedRadioButtonId());
        String rbselectedstring = "Weekdays";
        if(rbselected!=null) {
           rbselectedstring = rbselected.getText().toString();
        }
         Availability=0;
        if(rbselectedstring.contentEquals("Weekdays")){
            Availability=0;
        }else if(rbselectedstring.contentEquals("Weekends")){
            Availability=1;
        }else{
            Availability=2;
        }

         starttime = etstarttime.getText().toString();
         endtime = etendtime.getText().toString();

         latitude = latlang.latitude;
         longitude = latlang.longitude;

        //SnaxPlacerating

        Toast.makeText(this,SnaxStringsMultiSelectionSpinner.toString()+" "+landmark+" "+Snaxplacename+" "+desc+" "+String.valueOf(Availability)+" "+starttime+" "+endtime+" "+latitude+" "+longitude+" "+SnaxPlacerating , Toast.LENGTH_SHORT).show();
        try {
            new SubmitSnaxInfoTask().execute();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        float zoomlevel = 15;
        if (marker != null) {
            marker.remove();
        } else {
            zoomlevel = 12;
        }
        marker = googleMap.addMarker(new MarkerOptions()
                .position(latlang)
                .title("Snack Place"));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                latlang).zoom(zoomlevel).build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void createSpinner(List<Snack> snackRecords) {
        String[] snackArray = new String[snackRecords.size()];
        int count = 0;
        for (Snack snack : snackRecords) {
            snackArray[count++] = snack.SnackType;
        }

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.mySpinner);
        multiSelectionSpinner.setItems(snackArray);
        //multiSelectionSpinner.setSelection(new int[]{2, 6});

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public class SubmitSnaxInfoTask extends BaseAsyncRequest {

        public SubmitSnaxInfoTask() throws ApiException{
            callerName = "SubmitSnaxInfoTask";//any name

            serviceName = AppConstants.DB_SVC; //dreamfactory service base url
            endPoint = "tblSnackPlace"; //db table

            verb = "POST"; //type of request

            Tblsnackplace tblsnackplace = new Tblsnackplace();
            tblsnackplace.SnackPlaceName = Snaxplacename;
            tblsnackplace.SnackType = SnaxStringsMultiSelectionSpinner.toString();
            tblsnackplace.Locality = "to be implemented";
            tblsnackplace.LandMark = landmark;
            tblsnackplace.Description = desc;
            tblsnackplace.DaysAvailable =Availability;
            tblsnackplace.StartTimings = starttime;
            tblsnackplace.EndTimings = endtime;
            tblsnackplace.GoogleMapsName="dont know";
            tblsnackplace.GoogleMapsAddress = "To be extracted";
            tblsnackplace.GoogleMapsLatitude = latitude;
            tblsnackplace.GoogleMapsLongitude = longitude;
            tblsnackplace.ImagePath = "no value";
            tblsnackplace.PlaceID = placeID;

            requestString = ApiInvoker.serialize(tblsnackplace);
            // include API key and sessionToken
            applicationApiKey = AppConstants.API_KEY; //api key required to get the data
            sessionToken = PrefUtil.getString(getApplicationContext(), AppConstants.SESSION_TOKEN); //sessiontoken
        }

        @Override
        protected void onCompletion(boolean success) {
            Toast.makeText(AddSnackPlace.this, "Snack Place Added", Toast.LENGTH_LONG).show();
        }
    }

    public class GetSnackTypeTask extends BaseAsyncRequest {

        private Snacks snackRecords;

        public GetSnackTypeTask() {
            callerName = "getSnackInfoTask";//any name

            serviceName = AppConstants.DB_SVC; //dreamfactory service base url
            endPoint = "tblSnackType"; //db table

            verb = "GET"; //type of request

            queryParams = new HashMap<>();
            // filter to only the contact_info records related to the contact
            //queryParams.put("filter", "email=" + email + "%20AND%20password=" + password + ""); //where conditions

            // include API key and sessionToken
            applicationApiKey = AppConstants.API_KEY; //api key required to get the data
            sessionToken = PrefUtil.getString(getApplicationContext(), AppConstants.SESSION_TOKEN); //sessiontoken
        }

        @Override
        protected void processResponse(String response) throws ApiException {
            // results come back as an array of contact_info records
            // form is:
            // {
            //      "resource":[
            //          { contactInfoRecord }
            //      ]
            // }
            snackRecords = (Snacks) ApiInvoker.deserialize(response, "", Snacks.class);
        }

        @Override
        protected void onCompletion(boolean success) {
            if (success) {
                if (snackRecords.snackRecord.size() > 0)
                    createSpinner(snackRecords.snackRecord);
                else
                    createSpinner(snackRecords.snackRecord);
                progressDialog.dismiss();

            }
        }
    }

}
