package com.remainder.events.unotifier;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.model.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.remainder.events.unotifier.Fragments.CreateEventFragment;
import com.remainder.events.unotifier.Fragments.ProfileFragment;
import com.remainder.events.unotifier.Fragments.RetrieveFragment;
import com.remainder.events.unotifier.R;

import java.util.ArrayList;

import pub.devrel.easypermissions.EasyPermissions;

import static java.lang.Integer.parseInt;

public class EventActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,CreateEventFragment.OnFragmentInteractionListener,RetrieveFragment.OnFragmentInteractionListener,ProfileFragment.OnFragmentInteractionListener {
    ImageView imageView;
    TextView name1,email;
    String userId,userEmail,urlPhoto;
    ArrayList<Event> eventArrayList;
   GoogleAccountCredential mCredential;
   ProgressDialog progressDialog;
   FirebaseFirestore db;
    GoogleSignInResult googleSignInResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        eventArrayList=new ArrayList<>();
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(EventActivity.this);
        db=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        final Intent intent=getIntent();
        if(intent.getStringExtra("google")!=null)
        {
            PreferenceManager.getDefaultSharedPreferences(EventActivity.this).edit().putString("google", intent.getStringExtra("google")).apply();
            Toast.makeText(EventActivity.this,intent.getStringExtra("google")+" I am working",Toast.LENGTH_LONG).show();
            //mCredential.setSelectedAccountName(intent.getStringExtra("google"));
        }
        if(intent.getParcelableExtra("cohort")!=null)
        {
            CreateEventFragment createEventFragment = new CreateEventFragment();
            Bundle bundle=new Bundle();
            bundle.putParcelable("cohort",intent.getParcelableExtra("cohort"));
            createEventFragment.setArguments(bundle);
            loadFragment(createEventFragment);
        }
        else if(intent.getParcelableExtra("duplicate")!=null)
        {
            CreateEventFragment createEventFragment=new CreateEventFragment();
            Bundle bundle=new Bundle();
            bundle.putParcelable("duplicate",intent.getParcelableExtra("duplicate"));
            createEventFragment.setArguments(bundle);
            loadFragment(createEventFragment);
        }
        else if(intent.getParcelableExtra("slack")!=null)
        {
            CreateEventFragment createEventFragment = new CreateEventFragment();
            Bundle bundle=new Bundle();
            bundle.putParcelable("slack",intent.getParcelableExtra("slack"));
            createEventFragment.setArguments(bundle);
            loadFragment(createEventFragment);
        }
        else if(intent.getParcelableExtra("cohort_p")!=null)
        {
            CreateEventFragment createEventFragment = new CreateEventFragment();
            Bundle bundle=new Bundle();
            bundle.putParcelable("cohort_p",intent.getParcelableExtra("cohort_p"));
            createEventFragment.setArguments(bundle);
            loadFragment(createEventFragment);
        }
        else {
            CreateEventFragment createEventFragment = new CreateEventFragment();
            loadFragment(createEventFragment);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.add_event) {
            // Handle the camera action
            CreateEventFragment createEventFragment=new CreateEventFragment();
            loadFragment(createEventFragment);
        } else if (id == R.id.get_events) {
            RetrieveFragment retrieveFragment=new RetrieveFragment();
            loadFragment(retrieveFragment);

        } else if (id == R.id.profile) {
             ProfileFragment profileFragment=new ProfileFragment();
             loadFragment(profileFragment);
        }
        return true;
    }



    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */


    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                   mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
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
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }*/
    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    private void loadFragment(Fragment fragment) {
// create a FragmentManager
        FragmentManager fm = getFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
