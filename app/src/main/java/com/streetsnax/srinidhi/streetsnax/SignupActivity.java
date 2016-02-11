package com.streetsnax.srinidhi.streetsnax;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.streetsnax.srinidhi.streetsnax.models.User;
import com.streetsnax.srinidhi.streetsnax.models.Users;
import com.streetsnax.srinidhi.streetsnax.utilities.AppConstants;
import com.streetsnax.srinidhi.streetsnax.utilities.PasswordHash;
import com.streetsnax.srinidhi.streetsnax.utilities.PrefUtil;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import dfapi.ApiException;
import dfapi.ApiInvoker;
import dfapi.BaseAsyncRequest;


public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    public String signUpemail;
    public String signUppassword;
    public String signUpname;
    EditText _nameText, _emailText, _passwordText;
    Button _signupButton;
    TextView _loginLink;
    ProgressDialog progressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        _nameText = (EditText) findViewById(R.id.input_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);


        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidekeyboard();
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });


    }


    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.MyMaterialTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        signUpname = _nameText.getText().toString().trim();
        signUpemail = _emailText.getText().toString().trim();
        signUppassword = _passwordText.getText().toString().trim();

        // TODO: Implement your own signup logic here.
        // TODO: connect do DB and insert there.

        new GetLoginInfoTask().execute();
//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // On complete call either onSignupSuccess or onSignupFailed
//                        // depending on success
//                        onSignupSuccess();
//                        // onSignupFailed();
//                        progressDialog.dismiss();
//                        _signupButton.setEnabled(true);
//                    }
//                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
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

    public boolean validate() {

        Log.d("in validate", "first line");
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public class GetLoginInfoTask extends BaseAsyncRequest {

        private Users userRecords;

        public GetLoginInfoTask() {
            callerName = "getLoginInfoTask";//any name

            serviceName = AppConstants.DB_SVC; //dreamfactory service base url
            endPoint = "Users"; //db table

            verb = "GET"; //type of request

            queryParams = new HashMap<>();
            // filter to only the contact_info records related to the contact
            queryParams.put("filter", "email=" + signUpemail); //where conditions

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
            userRecords = new Users();
            Log.v("JSONResponse", response.trim());
            String JsonTestResponse = "{\"resource\":[]}";
            Log.v("JSONTestResponse", JsonTestResponse);
            if (!response.trim().equals(JsonTestResponse)) {
                Log.v("JSONResponse", "true");
                userRecords = (Users) ApiInvoker.deserialize(response, "", Users.class);
            }
        }

        @Override
        protected void onCompletion(boolean success) {
            if (success) {
                if (userRecords.userRecord.size() > 0) {
                    onSignupFailed();
                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), "User already exists", Toast.LENGTH_LONG).show();
                    _signupButton.setEnabled(true);
                } else {
                    new InsertLogonInfoTask().execute();
                }

            }
        }
    }

    public class InsertLogonInfoTask extends BaseAsyncRequest {

        private Users userRecords;

        @Override
        protected void doSetup() throws ApiException {
            try {
                callerName = "getLoginInfoTask";//any name

                serviceName = AppConstants.DB_SVC; //dreamfactory service base url
                endPoint = "Users"; //db table

                verb = "POST"; //type of request

                // build user record, don't have id yet so can't provide one
                User userRecord = new User();
                userRecord.UserName = signUpname;
                userRecord.Email = signUpemail;
                userRecord.Password = PasswordHash.createHash(signUppassword);
                userRecord.Active = true;
                userRecord.TypeOfLogin = 0;

                requestString = ApiInvoker.serialize(userRecord);
                queryParams = new HashMap<>();
                // filter to only the contact_info records related to the contact
                queryParams.put("id_field", "UserID");
                queryParams.put("fields", "*");

                Log.v("APIRequestString", requestString);
                // include API key and sessionToken
                applicationApiKey = AppConstants.API_KEY; //api key required to get the data
                sessionToken = PrefUtil.getString(getApplicationContext(), AppConstants.SESSION_TOKEN); //sessiontoken
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void processResponse(String response) throws ApiException, org.json.JSONException {
            Log.v("Signuup", "ProcessResponse");
            Log.v("JSONSignUpResponse", response.trim());
            String JsonTestResponse = "{\"resource\":[]}";
            Log.v("JSONTestResponse", JsonTestResponse);
            if (!response.trim().equals(JsonTestResponse))
                userRecords = (Users) ApiInvoker.deserialize(response, "", Users.class);
        }

        @Override
        protected void onCompletion(boolean success) {
            if (success) {
                Log.v("Signuup", "onCompletion");
                if (userRecords.userRecord.size() > 0) {
                    progressDialog.dismiss();
                    _signupButton.setEnabled(true);
                    onSignupSuccess();
                } else {
                    onSignupFailed();
                    progressDialog.dismiss();
                    _signupButton.setEnabled(true);
                }

            }
        }
    }
}