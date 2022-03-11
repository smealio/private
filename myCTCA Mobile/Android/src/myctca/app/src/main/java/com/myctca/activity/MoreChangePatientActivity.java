package com.myctca.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.common.MyCTCAActivity;
import com.myctca.common.fingerprintauth.FingerprintHandler;
import com.myctca.common.fingerprintauth.KeystoreHandler;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.fragment.MoreChangePatientFragment;
import com.myctca.model.AccessToken;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.IdentityUser;
import com.myctca.model.MyCTCAUserProfile;
import com.myctca.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class MoreChangePatientActivity extends MyCTCAActivity {


    private static final String TAG = "MyCTCA-MORECHGPATIENT";

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, MoreChangePatientActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CTCAAnalyticsManager.createEvent("MoreChangePatientActivity:onCreate", CTCAAnalyticsConstants.PAGE_CHANGE_PATIENT_VIEW, null, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_change_patient);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.more_change_patient_fragment_container);

        if (fragment == null) {
            fragment = new MoreChangePatientFragment();
            fm.beginTransaction()
                    .add(R.id.more_change_patient_fragment_container, fragment)
                    .commit();
        }

        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);
        selectedFragment = fragment;
        fragmentName = selectedFragment.getClass().getSimpleName();
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_more_change_patient, menu);

        String toolbarTitle = getString(R.string.more_change_patient_title);
        setToolBar(toolbarTitle);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "BACK BUTTON PRESSED");
                this.onBackPressed();
                break;
            default:
                Log.d(TAG, "DEFAULT");
                break;
        }

        return true;
    }

    @Override
    public void finish() {
        super.finish();
        onLeaveThisActivity();
    }

    protected void onLeaveThisActivity() {
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    public void changeDisplayedPatient(final String uniqueId) {

        // Show ProgressBar and Disable Activity
        // ActivityIndicator
        showActivityIndicator("Switching to new patient...");

        final String username = AppSessionManager.getInstance().getIdentityUser().getEmail();
        final String password = AppSessionManager.getInstance().getIdentityUser().getPword();

        // Get URL
        String url = BuildConfig.server_ctca_host + getString(R.string.endpoint_authorize);
        Log.d(TAG, "changeDisplayedPatient url: " + url);
        Log.d(TAG, "changeDisplayedPatient: " + username + "::" + password);

        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                hideActivityIndicator();
                // Result handling
                // Access token will be stored after successful user info retrieval
                AccessToken accessToken = new Gson().fromJson(response, AccessToken.class);

                Log.d(TAG, "AccessToken: " + accessToken.prettyPrint());
                retrieveUserProfile(accessToken, AppSessionManager.getInstance().getIdentityUser(), password);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideActivityIndicator();
                // Error handling
                Log.d(TAG, "Something went wrong while trying to get Access Token! Error: " + error.toString() + "::" + error.getLocalizedMessage());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    FingerprintHandler.getInstance().resetFingerprintAuthorization();
                    KeystoreHandler.getInstance().resetKeystorePreference();
                }
                doAcquireAccessTokenError(error);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("client_id", AppSessionManager.getInstance().getclientId());
                params.put("client_secret", AppSessionManager.getInstance().getclientSecret());
                params.put("grant_type", "password");
                params.put("scope", "openid profile email api external.identity.readwrite impersonation");
                params.put("acr_values=impersonate:", uniqueId);

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.INITIAL_TIMEOUT_MS, Constants.MAX_NUM_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Get Volley Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Add the request to the queue
        requestQueue.add(stringRequest);
    }

    private void doAcquireAccessTokenError(VolleyError error) {

        String errorMsg = VolleyErrorHandler.handleError(error, this);
        Log.d(TAG, "VolleyErrorHandler: " + errorMsg);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.error_sign_in_incomplete_title)
                .setMessage(errorMsg)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        View view = findViewById(R.id.login_form_scroll);
                        if (view != null) {
                            view.refreshDrawableState();
                        }
                    }
                });
        if (!isFinishing())
            builder.show();
    }

    private void retrieveUserProfile(final AccessToken accessToken, final IdentityUser iUser, final String password) {
        // ActivityIndicator
        showActivityIndicator("Retrieving User Profile...");

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = BuildConfig.myctca_server + getString(R.string.endpoint_user_profile);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                hideActivityIndicator();
                MyCTCAUserProfile userProfile = new Gson().fromJson(response, MyCTCAUserProfile.class);

//                JsonObject responseJson = JsonParser.parseString(response).getAsJsonObject();
//                MyCTCAUserProfile userProfile = new MyCTCAUserProfile(responseJson);

                Log.d(TAG, "MoreChangePatientActivity retrieveUserProfile DONE");
//                retrieveFacilityData(accessToken, userProfile, iUser, password);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideActivityIndicator();
                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_LONG).show();
                Log.e(TAG, error.toString());
                NetworkResponse response = error.networkResponse;
                if (response != null) {

                    String json = new String(response.data);
                    Log.e(TAG, response.statusCode + " " + json);
                }

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", accessToken.getTokenType() + " " + accessToken.getToken());
                return params;
            }


        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.INITIAL_TIMEOUT_MS, Constants.MAX_NUM_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
}
