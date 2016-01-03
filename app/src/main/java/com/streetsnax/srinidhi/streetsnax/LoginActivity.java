package com.streetsnax.srinidhi.streetsnax;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;
import com.streetsnax.srinidhi.streetsnax.models.Users;
import com.streetsnax.srinidhi.streetsnax.utilities.AppConstants;
import com.streetsnax.srinidhi.streetsnax.utilities.PasswordHash;
import com.streetsnax.srinidhi.streetsnax.utilities.PrefUtil;

import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;

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
        // Get the ViewFlipper
        mViewFlipper = (ViewFlipper) findViewById(R.id.loginviewFlipper);
        // Add all the images to the ViewFlipper
        for (int i = 0; i < 5; i++) {
            ImageView imageView = new ImageView(this);
//            android:adjustViewBounds="true"
//            android:scaleType="fitCenter"
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(lp);
            String imageUrl = "http://lorempixel.com/420/300/food/streetsnax" + i;
            Picasso.with(this).load(imageUrl).into(imageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                }

                @Override
                public void onError() {

                }
            });
            //new DownloadImageTask(imageView).execute("http://lorempixel.com/480/320/food/");
            //imageView.setImageResource(resources[i]);
            mViewFlipper.addView(imageView);
        }

        // Set in/out flipping animations
        mViewFlipper.setInAnimation(this, android.R.anim.fade_in);
        mViewFlipper.setOutAnimation(this, android.R.anim.fade_out);
        mViewFlipper.setAutoStart(true);
        mViewFlipper.setFlipInterval(3000); // flip every 2 seconds (2000ms)
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v("StreetMotion", "true");
        if (mGestureDetector.onTouchEvent(event))
            return true;
        else
            return super.onTouchEvent(event);
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
            queryParams.put("filter", "email=" + email.trim()); //where conditions

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
                Boolean isValidUser = false;
                if (userRecords.userRecord.size() > 0) {
                    try {
                        String existingPassword = userRecords.userRecord.get(0).Password;
                        if (PasswordHash.validatePassword(password, existingPassword))
                            isValidUser = true;
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                }
                if (isValidUser)
                    onLoginSuccess();
                else
                    onLoginFailed();
                progressDialog.dismiss();
                _loginButton.setEnabled(true);
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
    }

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            Log.v("StreetMotion", "true in filing");
            // Swipe left (next)
            if (e1.getX() > e2.getX()) {
                mViewFlipper.showNext();
            }

            // Swipe right (previous)
            if (e1.getX() < e2.getX()) {
                mViewFlipper.showPrevious();
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }
    }

}
