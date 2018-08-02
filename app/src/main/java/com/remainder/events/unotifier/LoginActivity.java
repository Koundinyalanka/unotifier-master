package com.remainder.events.unotifier;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.remainder.events.unotifier.Helpers.Constants;
import com.remainder.events.unotifier.R;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;




/*This Activity is used to get permissions and Signin Using Google
* Permissions must be granted first
* Then we must login with Google*/
public class LoginActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Button login;
    private FirebaseAuth mAuth;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    public GoogleAccountCredential mCredential;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    GoogleApiClient mGoogleApiClient;
    String accountName;
    private static Button googleSignIn;
    private static final String[] SCOPES = { CalendarScopes.CALENDAR };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EasyPermissions.requestPermissions(
                this,
                "This app needs to access your Google account (via Contacts).",
                Constants.REQUEST_PERMISSION_GET_ACCOUNTS,
                Manifest.permission.GET_ACCOUNTS);
        login = (Button) findViewById(R.id.login);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        Toast.makeText(LoginActivity.this,mCredential.getSelectedAccountName(),Toast.LENGTH_LONG).show();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //signInwithGoogle();
                chooseAccount();
                //Toast.makeText(LoginActivity.this,mCredential.getSelectedAccountName(),Toast.LENGTH_LONG).show();
                signInwithGoogle();
            }
        });
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    protected void signInwithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("804546696588-bbl9jen7rv6ah9qg3fcoq4cesgjkpgrc.apps.googleusercontent.com")
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                .enableAutoManage(LoginActivity.this, LoginActivity.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                //TO USE
                String personName = acct.getDisplayName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
                //String personPhoneURL = acct.getPhotoUrl().toString();
                //                User user = new User();
                //                user.setUsername(personName);
                //                user.setEmail(personEmail);
//                user.setPersonId(personId);
//                user.setPersonProfileUrl(personPhoneURL);
//                user.setSignedInWithGoogle(true);

                // updateFirebaseData(user,personEmail);

                /*Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Uri.class, new UriSerializer())
                        .create();*/

//                String userData = gson.toJson(user);
//                      EPreferenceManager.getSingleton().setUserdata(getActivity(),userData);
                firebaseAuthWithGoogle(acct);
            } else {
                Toast.makeText(LoginActivity.this, "There was a trouble signing in-Please try again "+result.getStatus(), Toast.LENGTH_SHORT).show();
                ;
            }
        }
        if(requestCode==Constants.REQUEST_ACCOUNT_PICKER)
        {
            if (resultCode == RESULT_OK && data != null &&
                    data.getExtras() != null) {
                String accountName =
                        data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                if (accountName != null) {
                    SharedPreferences settings =
                            getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(PREF_ACCOUNT_NAME, accountName);
                    editor.apply();
                    mCredential.setSelectedAccountName(accountName);
                    //getResultsFromApi();
                }
            }
        }

    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication pass.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, EventActivity.class);
                            accountName=mCredential.getSelectedAccountName();
                            intent.putExtra("google",accountName);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @AfterPermissionGranted(Constants.REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, android.Manifest.permission.GET_ACCOUNTS)) {
            accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                Toast.makeText(LoginActivity.this,mCredential.getSelectedAccountName(),Toast.LENGTH_LONG).show();
                //getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        Constants.REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    Constants.REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }
}
