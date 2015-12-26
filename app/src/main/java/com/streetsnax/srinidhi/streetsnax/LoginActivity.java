package com.streetsnax.srinidhi.streetsnax;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.streetsnax.srinidhi.streetsnax.models.Users;

import java.util.HashMap;

import dfapi.ApiException;
import dfapi.ApiInvoker;
import dfapi.BaseAsyncRequest;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    public String email;
    public String password;
    EditText _emailText, _passwordText;
    Button _loginButton;
    TextView _signupLink;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidekeyboard();
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.
        new GetLoginInfoTask().execute();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                Toast.makeText(LoginActivity.this, "Signed UP!", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

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
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

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
            callerName = "getLoginInfoTask";

            serviceName = AppConstants.DB_SVC;
            endPoint = "Users";

            verb = "GET";

            queryParams = new HashMap<>();
            // filter to only the contact_info records related to the contact
            queryParams.put("filter", "email=" + email + "%20AND%20password=" + password + "");

            // include API key and sessionToken
            applicationApiKey = AppConstants.API_KEY;
            sessionToken = PrefUtil.getString(getApplicationContext(), AppConstants.SESSION_TOKEN);
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
            userRecords = (Users) ApiInvoker.deserialize(response, "", Users.class);
        }

        @Override
        protected void onCompletion(boolean success) {
            if (success) {
                if (userRecords.userRecord.size() > 0)
                    onLoginSuccess();
                else
                    onLoginFailed();
                progressDialog.dismiss();
                _loginButton.setEnabled(true);
            }
        }
    }
}
