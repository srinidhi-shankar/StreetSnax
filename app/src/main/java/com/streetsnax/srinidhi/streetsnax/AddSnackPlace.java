package com.streetsnax.srinidhi.streetsnax;


import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.streetsnax.srinidhi.streetsnax.models.Snack;
import com.streetsnax.srinidhi.streetsnax.models.Snacks;
import com.streetsnax.srinidhi.streetsnax.models.Users;

import java.util.HashMap;
import java.util.List;

import dfapi.ApiException;
import dfapi.ApiInvoker;
import dfapi.BaseAsyncRequest;

public class AddSnackPlace extends AppCompatActivity {

    private MultiSelectionSpinner multiSelectionSpinner;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_snack);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Add Snack Place");
        setSupportActionBar(myToolbar);

        progressDialog = new ProgressDialog(AddSnackPlace.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Retrieving data...");
        progressDialog.show();

        new GetLoginInfoTask().execute();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class GetLoginInfoTask extends BaseAsyncRequest {

        private Snacks snackRecords;

        public GetLoginInfoTask() {
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

   public void createSpinner(List<Snack> snackRecords){
       String[] snackArray = new String[snackRecords.size()];
       int count=0;
       for(Snack snack:snackRecords){
         snackArray[count++] = snack.SnackType;
       }

       //String[] array = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "six", "seven", "eight", "nine", "ten"};
       multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.mySpinner);
       multiSelectionSpinner.setItems(snackArray);
       //multiSelectionSpinner.setSelection(new int[]{2, 6});
   }
}
