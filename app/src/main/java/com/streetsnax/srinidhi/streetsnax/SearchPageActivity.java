package com.streetsnax.srinidhi.streetsnax;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.streetsnax.srinidhi.streetsnax.adapters.ItemListBaseAdapter;
import com.streetsnax.srinidhi.streetsnax.adapters.PlacesAutoCompleteAdapter;
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

public class SearchPageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    //region Variables
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(12.778322, 77.368469), new LatLng(13.205187, 77.812042));//(LatLng southwest, LatLng northeast)
    protected GoogleApiClient mGoogleApiClient;
    private LinearLayoutManager mLinearLayoutManager;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private MultiSelectionSpinner multiSelectionSpinner;
    private LinearLayout searchLayoutView;
    private MenuItem myActionMenuItem;
    private TextView textViewTitle;
    private RelativeLayout snackLayout;
    private HorizontalScrollView snackHorizontalScrollView;
    private LinearLayout snackLayoutLinear;
    private ScrollView ScrollViewSearchPageContent;
    private ListView searchListView;
    private ImageView searchImageContent;
    private TextView searchTextContent;
    private TextView snackPlaceName;
    private ProgressBar searchPageProgressBar;
    private ArrayList<ItemDetails> item_details;
    private TextView textViewHiddenPlaceID;
    private String latlong;
    private GooglePlaceAutoComplete mPlace;
    private String placeAddress;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        setContentView(R.layout.activity_search_page);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        searchLayoutView = (LinearLayout) findViewById(R.id.searchLayoutView);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        snackLayout = (RelativeLayout) findViewById(R.id.snackLayout);
        snackHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.snackHorizontalScrollView);
        snackLayoutLinear = (LinearLayout) findViewById(R.id.snackLayoutLinear);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        ScrollViewSearchPageContent = (ScrollView) findViewById(R.id.ScrollViewSearchPageContent);
        searchListView = (ListView) findViewById(R.id.searchListView);
        searchImageContent = (ImageView) findViewById(R.id.searchImageContent);
        searchTextContent = (TextView) findViewById(R.id.searchTextContent);
        searchPageProgressBar = (ProgressBar) findViewById(R.id.searchPageProgressBar);
        textViewHiddenPlaceID = (TextView) findViewById(R.id.textViewHiddenPlaceID);
        snackPlaceName = (TextView) findViewById(R.id.snackPlaceName);
        //ScrollViewSearchPageContent.setVisibility(View.INVISIBLE);
        snackHorizontalScrollView.setVisibility(View.INVISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mPlace = (GooglePlaceAutoComplete) findViewById(R.id.googlePlacesAutoComplete);
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.mySpinner);
        new GetSnackTypeTask().execute();
        multiSelectionSpinner.setVisibility(View.INVISIBLE);

        mPlace.setOnPlaceClickListener(new GooglePlaceAutoComplete.OnPlaceClickListener() {
            @Override
            public void onPlaceClick(AdapterView<?> parent, net.sf.sprockets.google.Place.Prediction place, int position) {
                hidekeyboard();
                if (place != null) {
                    setTitle(mPlace.getText());
                    mPlace.setVisibility(View.INVISIBLE);
                    getSupportActionBar().show();
                    final String placeId = String.valueOf(place.getPlaceId().getId());
                    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
                    placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getCount() == 1) {
                                latlong = String.valueOf(places.get(0).getLatLng());
                                textViewHiddenPlaceID.setText(placeId);
                                myActionMenuItem.collapseActionView();
                                placeAddress = places.get(0).getAddress().toString();
                                setTitle(places.get(0).getName());
                                mPlace.setText("");
                                textViewTitle.setText("Choose Snacks..");
                                textViewTitle.setVisibility(View.VISIBLE);
                                snackHorizontalScrollView.setVisibility(View.INVISIBLE);
                                snackLayout.setVisibility(View.VISIBLE);
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
                List<String> snackList = multiSelectionSpinner.getSelectedStrings();
                searchPageProgressBar.setVisibility(View.VISIBLE);
                //searchListView.removeAllViews();
                if (snackLayoutLinear.getChildCount() > 0)
                    snackLayoutLinear.removeAllViews();
                if (textViewTitle.getText().length() <= 1) {
                    textViewTitle.setText("Choose Snacks..");
                    textViewTitle.setVisibility(View.VISIBLE);
                    snackHorizontalScrollView.setVisibility(View.INVISIBLE);
                } else {
                    snackHorizontalScrollView.setVisibility(View.VISIBLE);
                    textViewTitle.setVisibility(View.INVISIBLE);
                    for (int i = 0; i < snackList.size(); i++) {
                        TextView rowTextView = new TextView(SearchPageActivity.this);
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
                    searchListView.setAdapter(new ItemListBaseAdapter(SearchPageActivity.this, item_details));
                    searchPageProgressBar.setVisibility(View.INVISIBLE);
                    snackHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                }
                //searchLayoutView.setVisibility(View.INVISIBLE);

                //getActionBar().show();

            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchPageActivity.this, AddSnackPlace.class);
                startActivity(intent);
            }
        });
        snackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                multiSelectionSpinner.performClick();
                textViewTitle.setVisibility(View.INVISIBLE);
            }
        });

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = searchListView.getItemAtPosition(position);
                ItemDetails obj_itemDetails = (ItemDetails) o;
                Intent intent = new Intent(SearchPageActivity.this, SearchScrollingActivity.class);
                intent.putExtra("SnackPlaceDetails", obj_itemDetails);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_page, menu);
        myActionMenuItem = menu.findItem(R.id.action_search);
        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //callSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//              if (searchView.isExpanded() && TextUtils.isEmpty(newText)) {
                callSearch(newText);
//              }
                return true;
            }

            public void callSearch(String query) {
                //Do searching
                //mRecyclerView.setVisibility(View.VISIBLE);
                if (!query.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mAutoCompleteAdapter.getFilter().filter(query.toString());
                } else if (!mGoogleApiClient.isConnected()) {
                    Toast.makeText(getApplicationContext(), AppConstants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
                    Log.e(AppConstants.PlacesTag, AppConstants.API_NOT_CONNECTED);
                }
            }

        });*/
        MenuItemCompat.setOnActionExpandListener(myActionMenuItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
//                if (mRecyclerView != null && mRecyclerView.getVisibility() == View.VISIBLE)
//                    mRecyclerView.setVisibility(View.INVISIBLE);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                if (!toolbar.getTitle().toString().toUpperCase().contains("SEARCH")) {
                    snackLayout.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
//            if (mRecyclerView != null && mRecyclerView.getVisibility() == View.VISIBLE)
//                mRecyclerView.setVisibility(View.INVISIBLE);
        } else if (id == R.id.action_search) {
            getSupportActionBar().hide();
            searchLayoutView.setVisibility(View.VISIBLE);
            snackLayout.setVisibility(View.INVISIBLE);
            mPlace.setVisibility(View.VISIBLE);
            mPlace.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    public void onClick(View v) {
//        if (v == deleteSnack) {
//            textViewTitle.setText("");
//            multiSelectionSpinner.setSelection(0);
//        }
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

    //region CustomMethods
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    public void hidekeyboard() {

        try {
            InputMethodManager inputManager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(
                    this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }


    public void createSpinner(List<Snack> snackRecords) {
        String[] snackArray = new String[snackRecords.size()];
        int count = 0;
        for (Snack snack : snackRecords) {
            snackArray[count++] = snack.SnackType;
        }
        multiSelectionSpinner.setItems(snackArray);

        //String[] array = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "six", "seven", "eight", "nine", "ten"};
        //multiSelectionSpinner.setSelection(new int[]{2, 6});
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
    //endregion

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
