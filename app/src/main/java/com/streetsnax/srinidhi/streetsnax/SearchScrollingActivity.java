package com.streetsnax.srinidhi.streetsnax;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.streetsnax.srinidhi.streetsnax.utilities.CommonHelper;
import com.streetsnax.srinidhi.streetsnax.utilities.ItemDetails;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SearchScrollingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView txtViewSnackTypes;
    private TextView txtViewAddress;
    private ImageView imageinfoview;
    private CollapsingToolbarLayout infotoolbar_layout;
    private LatLng location;
    private String MapTitle;
    private String MapDescription;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getIntent().getExtras();
        ItemDetails itemDetails = data.getParcelable("SnackPlaceDetails");
        setContentView(R.layout.activity_search_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtViewSnackTypes = (TextView) findViewById(R.id.txtViewSnackTypes);
        txtViewAddress = (TextView) findViewById(R.id.txtViewAddress);
        imageinfoview = (ImageView) findViewById(R.id.imageinfoview);
        infotoolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.infotoolbar_layout);

        List<String> snacklist = new ArrayList<>(Arrays.asList(itemDetails.getsnackType().split(",")));
        txtViewSnackTypes.setMaxLines(snacklist.size());
        txtViewSnackTypes.setText(itemDetails.getsnackType());
//        for (int i = 0; i < snacklist.size(); i++) {
//            txtViewSnackTypes.setText(txtViewSnackTypes.getText() + snacklist.get(i));
//            if (i != 0)
//                txtViewSnackTypes.setText(txtViewSnackTypes.getText() + "\n");
//        }
//        txtViewSnackTypes.setText("Snack Types Available : " +
//                itemDetails.getsnackType() +
//                "\r\n\r\n" +
//                "Address :" +
//                itemDetails.getplaceAddress());
        List<String> addresslist = new ArrayList<>(Arrays.asList(itemDetails.getplaceAddress().split(",")));
        txtViewAddress.setMaxLines(addresslist.size());
        txtViewAddress.setText("");
        for (int i = 0; i < addresslist.size(); i++) {
            String addressLine = CommonHelper.toTitleCase(addresslist.get(i).trim());
            if (!addressLine.equals(""))
                txtViewAddress.setText(txtViewAddress.getText() + addressLine + "\n");
        }

        Picasso.with(this).load(itemDetails.getItemImageSrc()).into(imageinfoview);
        imageinfoview.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);
        //new DownloadImageTask(imageinfoview).execute(itemDetails.getItemImageSrc());
        infotoolbar_layout.setTitle(itemDetails.getsnackPlaceName());
        MapTitle = itemDetails.getsnackPlaceName();
        MapDescription = itemDetails.getsnackPlaceName();
        String[] latLng = itemDetails.getlatlong().replace("lat/lng:", "").replace("(", "").replace(")", "").trim().split(",");
        latitude = Double.parseDouble(latLng[0]);
        longitude = Double.parseDouble(latLng[1]);
        location = new LatLng(latitude, longitude);

        try {
            MapFragment mapFragment = ((MapFragment) getFragmentManager().
                    findFragmentById(R.id.map));
            mapFragment.getMapAsync(SearchScrollingActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSearchInfo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //replace this with directions from current location
//                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + latitude + "," + longitude + "\""));
//                i.setClassName("com.google.android.apps.maps",
//                        "com.google.android.maps.MapsActivity");
//                startActivity(i);

                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", latitude, longitude, infotoolbar_layout.getTitle());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    try {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(unrestrictedIntent);
                    } catch (ActivityNotFoundException innerEx) {
                        Toast.makeText(SearchScrollingActivity.this, "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public void onMapReady(GoogleMap map) {

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for Activity#requestPermissions for more details.
//                return;
//            }
//        }
//        map.setMyLocationEnabled(true);
        map.setTrafficEnabled(true);
        map.setBuildingsEnabled(true);

        map.addMarker(new MarkerOptions()
                .title(MapTitle)
                .snippet(MapDescription)
                .position(location)).showInfoWindow();
        map.setPadding(10, 10, 10, 10);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "StreetSnax");
                String url = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude;
                String sAux = "\nHey find this awesome streetfood " + infotoolbar_layout.getTitle() + "\n\n";
                sAux = sAux + "address:\n\n" + txtViewAddress.getText() + "\n\n";
                sAux += "Maps: " + url + "\n\nFor more details install StreetSnax";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch (Exception e) { //e.toString();
            }
            return true;
        } else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
