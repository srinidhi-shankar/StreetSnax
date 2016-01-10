package com.streetsnax.srinidhi.streetsnax;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.streetsnax.srinidhi.streetsnax.models.Snack;
import com.streetsnax.srinidhi.streetsnax.models.Snacks;
import com.streetsnax.srinidhi.streetsnax.utilities.AppConstants;
import com.streetsnax.srinidhi.streetsnax.utilities.ItemDetails;
import com.streetsnax.srinidhi.streetsnax.utilities.MultiSelectionSpinner;
import com.streetsnax.srinidhi.streetsnax.utilities.PrefUtil;

import net.sf.sprockets.widget.GooglePlaceAutoComplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dfapi.ApiException;
import dfapi.ApiInvoker;
import dfapi.BaseAsyncRequest;

public class SearchSubmitChooseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //region Variables
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(12.778322, 77.368469), new LatLng(13.205187, 77.812042));//(LatLng southwest, LatLng northeast)
    protected GoogleApiClient mGoogleApiClient;
    private MultiSelectionSpinner multiSelectionSpinner;
    private TextView textViewTitle;
    private RelativeLayout snackLayout;
    private RelativeLayout searchLayout;
    private HorizontalScrollView snackHorizontalScrollView;
    private LinearLayout snackLayoutLinear;
    private TextView textViewGoogleSearch;
    private ArrayList<ItemDetails> item_details;
    private TextView textViewHiddenPlaceID;
    private String latlong;
    private GooglePlaceAutoComplete mPlace;
    private String placeAddress;
    private FloatingActionButton ibplus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        setContentView(R.layout.activity_search_submit_choose);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        searchLayout = (RelativeLayout) findViewById(R.id.searchLayout);
        snackLayout = (RelativeLayout) findViewById(R.id.snackLayout);
        snackHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.snackHorizontalScrollView);
        snackLayoutLinear = (LinearLayout) findViewById(R.id.snackLayoutLinear);
        textViewHiddenPlaceID = (TextView) findViewById(R.id.textViewHiddenPlaceID);
        textViewGoogleSearch = (TextView) findViewById(R.id.textViewGoogleSearch);
        ibplus = (FloatingActionButton) findViewById(R.id.ibplus);
        //ScrollViewSearchPageContent.setVisibility(View.INVISIBLE);
        snackHorizontalScrollView.setVisibility(View.INVISIBLE);
        mPlace = (GooglePlaceAutoComplete) findViewById(R.id.googlePlacesAutoComplete);
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.mySpinner);
        new GetSnackTypeTask().execute();
        multiSelectionSpinner.setVisibility(View.INVISIBLE);

        ibplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchSubmitChooseActivity.this, AddSnackPlace.class);
                startActivity(intent);
            }
        });

        mPlace.setOnPlaceClickListener(new GooglePlaceAutoComplete.OnPlaceClickListener() {
            @Override
            public void onPlaceClick(AdapterView<?> parent, net.sf.sprockets.google.Place.Prediction place, int position) {
                mPlace.clearFocus();
                hidekeyboard();
                if (place != null) {
                    //setTitle(mPlace.getText());
                    placeAddress = mPlace.getText().toString();
                    textViewGoogleSearch.setText(placeAddress);
                    searchLayout.setVisibility(View.VISIBLE);
                    mPlace.setVisibility(View.GONE);
                    snackLayout.setVisibility(View.VISIBLE);
                    final String placeId = String.valueOf(place.getPlaceId().getId());
                    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
                    placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getCount() == 1) {
                                latlong = String.valueOf(places.get(0).getLatLng());
                                textViewHiddenPlaceID.setText(placeId);
                                //placeAddress = places.get(0).getAddress().toString();
                                //setTitle(places.get(0).getName());
                                mPlace.setText("");
                                if (snackHorizontalScrollView.getVisibility() == View.VISIBLE)
                                    GetSnackResults();
                            } else {
                                Toast.makeText(getApplicationContext(), AppConstants.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        multiSelectionSpinner.alertbuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface dialog) {
                //Do some work
                textViewTitle.setText(multiSelectionSpinner.getSelectedItemsAsString());
                //searchPageProgressBar.setVisibility(View.VISIBLE);
                //searchListView.removeAllViews();
                if (textViewTitle.getText().length() <= 1) {
                    textViewTitle.setText("Choose Snacks..");
                    textViewTitle.setVisibility(View.VISIBLE);
                    snackHorizontalScrollView.setVisibility(View.INVISIBLE);
                } else {
                    snackHorizontalScrollView.setVisibility(View.VISIBLE);
                    textViewTitle.setVisibility(View.INVISIBLE);
                    GetSnackResults();
                    snackHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                }
                //searchLayoutView.setVisibility(View.INVISIBLE);
                //getActionBar().show();
            }
        });

        snackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                multiSelectionSpinner.performClick();
                textViewTitle.setVisibility(View.INVISIBLE);
            }
        });

        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlace.setVisibility(View.VISIBLE);
                mPlace.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                searchLayout.setVisibility(View.GONE);
            }
        });

        snackLayout.setVisibility(View.GONE);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v("Google API Callback", "Connection Done");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("Google API Callback", "Connection Suspended");
        Log.v("Code", String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v("Google API Callback", "Connection Failed");
        Log.v("Error Code", String.valueOf(connectionResult.getErrorCode()));
        Toast.makeText(this, AppConstants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            Log.v("Google API", "Connecting");
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            Log.v("Google API", "Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }

    public void hidekeyboard() {
        try {
            InputMethodManager inputManager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(
                    this.getCurrentFocus().getWindowToken(),
                    0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    private void GetSnackResults() {
        List<String> snackList = multiSelectionSpinner.getSelectedStrings();
        if (snackLayoutLinear.getChildCount() > 0)
            snackLayoutLinear.removeAllViews();
        for (int i = 0; i < snackList.size(); i++) {
            TextView rowTextView = new TextView(SearchSubmitChooseActivity.this);
            rowTextView.setText(snackList.get(i));
            //rowTextView.setAllCaps(true);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            params.setMargins(0, 0, 10, 0);
            rowTextView.setLayoutParams(params);
            rowTextView.setPadding(10, 10, 10, 10);
            rowTextView.setBackgroundResource(R.drawable.tags);
            snackLayoutLinear.addView(rowTextView);
        }
        item_details = GetSearchResults(placeAddress, textViewTitle.getText().toString());
        //searchListView.setAdapter(new ItemListBaseAdapter(SearchSubmitChooseActivity.this, item_details));
        //searchPageProgressBar.setVisibility(View.INVISIBLE);
    }

    private ArrayList<ItemDetails> GetSearchResults(String locationAddress, String snackType) {
        ArrayList<ItemDetails> results = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ItemDetails itemDetails = new ItemDetails();
            String imageSrc = "http://lorempixel.com/400/200/food/SnackPlaceName" + i;
            itemDetails.setItemImageSrc(imageSrc);
            itemDetails.setplaceAddress(locationAddress);
            itemDetails.setsnackType(snackType);
            itemDetails.setplaceID(textViewHiddenPlaceID.getText().toString());
            itemDetails.setlatlong(latlong);
            itemDetails.setsnackPlaceName("SnackPlaceName #" + i);
            results.add(itemDetails);
        }

        return results;
    }
    public void createSpinner(List<Snack> snackRecords) {
        String[] snackArray = new String[snackRecords.size()];
        int count = 0;
        for (Snack snack : snackRecords) {
            snackArray[count++] = snack.SnackType;
        }
        multiSelectionSpinner.setItems(snackArray);
    }

    //region DreamFactoryTask To Retrieve Snacks
    public class GetSnackTypeTask extends BaseAsyncRequest {

        private Snacks snackRecords;

        public GetSnackTypeTask() {
            callerName = "GetSnackTypeTask";//any name

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


            }
        }
    }
    //endregion
}
