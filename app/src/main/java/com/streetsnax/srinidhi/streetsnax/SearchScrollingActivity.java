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

import com.streetsnax.srinidhi.streetsnax.utilities.ItemDetails;

import java.io.InputStream;

public class SearchScrollingActivity extends AppCompatActivity {

    private TextView textViewSearchInfoContent;
    private ImageView imageinfoview;
    private CollapsingToolbarLayout infotoolbar_layout;
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
