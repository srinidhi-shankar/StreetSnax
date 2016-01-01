package com.streetsnax.srinidhi.streetsnax;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.streetsnax.srinidhi.streetsnax.utilities.ItemDetails;

import java.io.InputStream;

public class SearchScrollingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView textViewSearchInfoContent;
    private ImageView imageinfoview;
    private CollapsingToolbarLayout infotoolbar_layout;
    private LatLng location;
    private String MapTitle;
    private String MapDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getIntent().getExtras();
        ItemDetails itemDetails = data.getParcelable("SnackPlaceDetails");
        setContentView(R.layout.activity_search_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textViewSearchInfoContent = (TextView) findViewById(R.id.textViewSearchInfoContent);
        imageinfoview = (ImageView) findViewById(R.id.imageinfoview);
        infotoolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.infotoolbar_layout);
        textViewSearchInfoContent.setText("Snack Types Available : " +
                itemDetails.getsnackType() +
                "\r\n\r\n" +
                "Address :" +
                itemDetails.getplaceAddress());
        new DownloadImageTask(imageinfoview).execute(itemDetails.getItemImageSrc());
        infotoolbar_layout.setTitle(itemDetails.getsnackPlaceName());
        MapTitle = itemDetails.getsnackPlaceName();
        MapDescription = itemDetails.getsnackPlaceName();
        String[] latLng = itemDetails.getlatlong().replace("lat/lng:", "").replace("(", "").replace(")", "").trim().split(",");
        double latitude = Double.parseDouble(latLng[0]);
        double longitude = Double.parseDouble(latLng[1]);
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
