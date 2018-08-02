package com.remainder.events.unotifier.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.opencsv.CSVReader;
import com.remainder.events.unotifier.Helpers.CohortExpiry;
import com.remainder.events.unotifier.Helpers.CohortProject;
import com.remainder.events.unotifier.Helpers.CohortSlack;
import com.remainder.events.unotifier.Helpers.Constants;
import com.remainder.events.unotifier.Helpers.Upload;
import com.remainder.events.unotifier.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static java.lang.Integer.parseInt;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateEventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateEventFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    GoogleAccountCredential mCredential;
    FirebaseFirestore db;
    String userName;
    String name;
    EditText group;
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    BranchUniversalObject buo;
    AppCompatCheckBox checkBox;
    private Button mCallApiButton,uploadCSV;
    ProgressDialog mProgress;
    TextView nameOfFile;
    String email,nano,url1,url2;
    List<String> emailList;
    EditText fromDate,toDate,startTime,toTime,summary,location,description,emails,interval1,count1,notifyTime,nanoKey;
    private SimpleDateFormat dateFormatter;
    String summ,loc,desc,frequency;
    int interval,count,notify;
    private RadioGroup radioGroup;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    java.util.Calendar calendar;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CreateEventFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateEventFragment newInstance(String param1, String param2) {
        CreateEventFragment fragment = new CreateEventFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
       // selectCSVFile();

        buo= new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setTitle("My Content Title")
                .setContentDescription("My Content Description")
                .setContentImageUrl("https://lorempixel.com/400/400")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(new ContentMetadata().addCustomMetadata("key1", "value1"));
        LinkProperties lp = new LinkProperties()
                .setChannel("You")
                .setFeature("Sam")
                .setCampaign("Sla")
                .setStage("Uda")
                .addControlParameter("$desktop_url", "https://www.youtube.com/watch?v=DxZEhd9qH9k")
                .addControlParameter("custom", "data")
                .addControlParameter("custom_random", Long.toString(Calendar.getInstance().getTimeInMillis()));

        buo.generateShortUrl(getActivity(), lp, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    url1=url;
                    Log.i("BRANCH SDK", "got my Branch link to share: " + url);
                }
            }
        });
        if(mCredential!=null)
        {
            Toast.makeText(getActivity(),mCredential.getSelectedAccountName(),Toast.LENGTH_LONG).show();
        }
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        userName=firebaseUser.getDisplayName();

        //getting firebase objects
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(userName);
        db=FirebaseFirestore.getInstance();
        fromDate=(EditText)view.findViewById(R.id.from_date);
        toDate=(EditText)view.findViewById(R.id.to_date);
        nameOfFile=(TextView)view.findViewById(R.id.nameofFile);
        startTime=(EditText)view.findViewById(R.id.from_time);
        toTime=(EditText)view.findViewById(R.id.to_time);
        summary=(EditText)view.findViewById(R.id.summary);
        location=(EditText)view.findViewById(R.id.location);
        interval1=(EditText)view.findViewById(R.id.interval);
        count1=(EditText)view.findViewById(R.id.count);
        description=(EditText)view.findViewById(R.id.description);
        emails=(EditText)view.findViewById(R.id.emails);
        checkBox=(AppCompatCheckBox)view.findViewById(R.id.acceptForce);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        notifyTime=(EditText)view.findViewById(R.id.notifyTime);
        mCallApiButton = (Button) view.findViewById(R.id.api_button);
        uploadCSV=(Button)view.findViewById(R.id.uploadCSV);
        group=(EditText)view.findViewById(R.id.group);
        SharedPreferences editor = getActivity().getSharedPreferences("group", MODE_PRIVATE);
        group.setText(editor.getString(" ","nogroup"));
        if(getArguments()!=null)
        {
            if(getArguments().getParcelable("cohort")!=null){
            CohortExpiry cohort=getArguments().getParcelable("cohort");
            emails.setText(cohort.getEmailList());
            location.setText("online");
            String[] token = cohort.getExpiryDate().split("/");
            int date=Integer.parseInt(token[1]);
            int month=Integer.parseInt(token[0]);
            int year=Integer.parseInt(token[2]);
            fromDate.setText(year+"-"+String.format("%02d-%02d",month ,date-1));
            fromDate.setText(year+"-"+String.format("%02d-%02d",month ,date-1));
            toDate.setText(year+"-"+String.format("%02d-%02d",month ,date-1));
            startTime.setText("18:00");
            toTime.setText("19:00");
            summary.setText("Your "+cohort.getNanodegree()+" Nanodegree expires on "+cohort.getExpiryDate()+"   [Learn to Code]");
            description.setText("Complete all the projects before the given due date to graduate from the nanodegree.");
                buo= new BranchUniversalObject()
                        .setCanonicalIdentifier("content/12345")
                        .setTitle("My Content Title")
                        .setContentDescription("My Content Description")
                        .setContentImageUrl("https://lorempixel.com/400/400")
                        .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                        .setContentMetadata(new ContentMetadata().addCustomMetadata("key1", "value1"));
                LinkProperties lp1 = new LinkProperties()
                        .setChannel("You")
                        .setFeature("Sam")
                        .setCampaign("Sla")
                        .setStage("Uda")
                        .addControlParameter("$desktop_url","https://classroom.udacity.com/me")
                        .addControlParameter("custom", "data")
                        .addControlParameter("custom_random", Long.toString(Calendar.getInstance().getTimeInMillis()));
                buo.generateShortUrl(getActivity(), lp1, new Branch.BranchLinkCreateListener() {
                    @Override
                    public void onLinkCreate(String url, BranchError error) {
                        if (error == null) {
                            description.append("\n"+"Join the Classroom using following link:"+"\n"+"\n"+ url);

                            Log.i("BRANCH SDK", "got my Branch link to share: " + url);
                        }
                    }
                });
            }
            else if(getArguments().getParcelable("duplicate")!=null)
            {
                com.remainder.events.unotifier.Helpers.Event event=getArguments().getParcelable("duplicate");
                summary.setText(event.getSumm());
                location.setText(event.getLoca());
                emails.setText(android.text.TextUtils.join(",", event.getAccepted())+","+android.text.TextUtils.join(",", event.getDeclined())+","
                        +android.text.TextUtils.join(",", event.getNeedsAction())+android.text.TextUtils.join(",",event.getTenative()));
                description.setText(event.getDesc());
            }
            else if(getArguments().getParcelable("slack")!=null)
            {
                CohortSlack cohortSlack=getArguments().getParcelable("slack");
                emails.setText(cohortSlack.getEmailList());
                location.setText("Online");
                startTime.setText("18:00");
                toTime.setText("19:00");
                summary.setText("Join your "+cohortSlack.getNanodegree()+" Nanodegree Slack Workspace "+"   [Learn to Code]");

                buo= new BranchUniversalObject()
                        .setCanonicalIdentifier("content/12345")
                        .setTitle("My Content Title")
                        .setContentDescription("My Content Description")
                        .setContentImageUrl("https://lorempixel.com/400/400")
                        .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                        .setContentMetadata(new ContentMetadata().addCustomMetadata("key1", "value1"));
                LinkProperties lp1 = new LinkProperties()
                        .setChannel("You")
                        .setFeature("Sam")
                        .setCampaign("Sla")
                        .setStage("Uda")
                        .addControlParameter("$desktop_url",cohortSlack.getSlackLink())
                        .addControlParameter("custom", "data")
                        .addControlParameter("custom_random", Long.toString(Calendar.getInstance().getTimeInMillis()));
                buo.generateShortUrl(getActivity(), lp1, new Branch.BranchLinkCreateListener() {
                    @Override
                    public void onLinkCreate(String url, BranchError error) {
                        if (error == null) {
                            description.setText("Join the Slack Workspace using following link:"+"\n"+"\n"+ url);

                            Log.i("BRANCH SDK", "got my Branch link to share: " + url);
                        }
                    }
                });

            }
            else if(getArguments().getParcelable("cohort_p")!=null)
            {
                CohortProject cohortSlack=getArguments().getParcelable("cohort_p");
                emails.setText(cohortSlack.getEmailList());
                location.setText("Online");
                startTime.setText("18:00");
                toTime.setText("19:00");

                //Toast.makeText(getActivity(),cohortSlack.getProjectName()+token[0],Toast.LENGTH_LONG).show();
                /*int date=Integer.parseInt(token[1]);
                int month=Integer.parseInt(token[0]);
                int year=Integer.parseInt(token[2]);*/
                fromDate.setText(cohortSlack.getProjectName());
                toDate.setText(cohortSlack.getProjectName());
                summary.setText("Submit your "+cohortSlack.getExpiryDate()+" by "+cohortSlack.getProjectName()+"   [Learn to Code]");

                buo= new BranchUniversalObject()
                        .setCanonicalIdentifier("content/12345")
                        .setTitle("My Content Title")
                        .setContentDescription("My Content Description")
                        .setContentImageUrl("https://lorempixel.com/400/400")
                        .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                        .setContentMetadata(new ContentMetadata().addCustomMetadata("key1", "value1"));
                LinkProperties lp1 = new LinkProperties()
                        .setChannel("You")
                        .setFeature("Sam")
                        .setCampaign("Sla")
                        .setStage("Uda")
                        .addControlParameter("$desktop_url","https://classroom.udacity.com/me")
                        .addControlParameter("custom", "data")
                        .addControlParameter("custom_random", Long.toString(Calendar.getInstance().getTimeInMillis()));
                buo.generateShortUrl(getActivity(), lp1, new Branch.BranchLinkCreateListener() {
                    @Override
                    public void onLinkCreate(String url, BranchError error) {
                        if (error == null) {
                            description.setText("Join the Classroom using following link:"+"\n"+"\n"+ url);

                            Log.i("BRANCH SDK", "got my Branch link to share: " + url);
                        }
                    }
                });

            }
        }


        if(getArguments()!=null) {
            Bundle bundle = getArguments();
            if (bundle.getStringArrayList("arraylist") != null) {
                emails.setText(android.text.TextUtils.join(",", bundle.getStringArrayList("arraylist")));
            }
        }
        //Toast.makeText(EventActivity.this,mCredential.getSelectedAccountName(),Toast.LENGTH_LONG).show();
        uploadCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPDF();
                //nano=nanoKey.getText().toString();
                //getLoaderManager().restartLoader(NANOKEY,null,CreateEventFragment.this);
                //location.setText(url1);
            }
        });
        mCallApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallApiButton.setEnabled(false);
                email=emails.getText().toString();
                    emailList = Arrays.asList(email.split(","));
                Bundle eventBundle=new Bundle();
                eventBundle.putString("summary",summary.getText().toString()+"   [Learn to Code]");
                eventBundle.putString("description",description.getText().toString());
                eventBundle.putString("location",location.getText().toString());
                eventBundle.putStringArrayList("emails",new ArrayList<String>(emailList));
                eventBundle.putString("start_date_time",fromDate.getText().toString()+"T"+startTime.getText().toString()+":00+05:30");
                eventBundle.putString("end_date_time",toDate.getText().toString()+"T"+toTime.getText().toString()+":00+05:30");
                eventBundle.putString("frequency",frequency);
                eventBundle.putInt("interval",parseInt(interval1.getText().toString()));
                eventBundle.putInt("count",parseInt(count1.getText().toString()));
                eventBundle.putInt("notifyTime",parseInt(notifyTime.getText().toString()));
                getResultsFromApi();
                getLoaderManager().restartLoader(Constants.SEND_EVENTS,eventBundle,CreateEventFragment.this);
                setRetainInstance(true);
                mCallApiButton.setEnabled(true);
            }
        });
        radioGroup=(RadioGroup)view.findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.weekly)
                    frequency="WEEKLY";
                if(i==R.id.daily)
                    frequency="DAILY";
            }
        });
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar= java.util.Calendar.getInstance();
                fromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        java.util.Calendar newDate= java.util.Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        fromDate.setText(dateFormatter.format(newDate.getTime()));
                    }

                },calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH),
                        calendar.get(java.util.Calendar.DAY_OF_MONTH));
                fromDatePickerDialog.show();
            }
        });
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar= java.util.Calendar.getInstance();
                toDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        java.util.Calendar newDate= java.util.Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        toDate.setText(dateFormatter.format(newDate.getTime()));
                    }

                },calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH),
                        calendar.get(java.util.Calendar.DAY_OF_MONTH));
                toDatePickerDialog.show();
            }
        });
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                java.util.Calendar mcurrentTime = java.util.Calendar.getInstance();
                int hour = mcurrentTime.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startTime.setText( String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Start Time");
                mTimePicker.show();
            }
        });
        toTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                java.util.Calendar mcurrentTime = java.util.Calendar.getInstance();
                int hour = mcurrentTime.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        toTime.setText( String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Start Time");
                mTimePicker.show();
            }
        });

        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Calling Google Calendar API ...");
        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getActivity(), Arrays.asList(Constants.SCOPES))
                .setBackOff(new ExponentialBackOff());
        mCredential.setSelectedAccountName(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("google", null));
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<String> onCreateLoader(int i, final Bundle bundle) {
        final com.google.api.services.calendar.Calendar mService;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
        switch (i)
        {
            case Constants.SEND_EVENTS:
                return new AsyncTaskLoader<String>(getActivity()) {
                    @Override
                    public String loadInBackground() {
                            if(mCredential!=null) {
                                try {
                                    createEvent(mCredential, bundle.getString("start_date_time"), bundle.getString("end_date_time")
                                            , bundle.getString("summary"), bundle.getString("location"), bundle.getString("description")
                                            , bundle.getStringArrayList("emails"), bundle.getString("frequency"),
                                            bundle.getInt("interval"), bundle.getInt("count"), bundle.getInt("notifyTime"));
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                System.out.print(mCredential.getSelectedAccountName());
                            }
                            else {
                                Log.e("------->","something happened");
                            }
                        return null;
                    }

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();

                        if(bundle==null)
                            return;
                        if (!getRetainInstance()){
                            forceLoad();
                            mProgress.show();
                        }
                    }


                };
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {
        switch (loader.getId())
        {
            case Constants.SEND_EVENTS:
                mProgress.dismiss();
                mProgress.hide();
                Toast.makeText(getActivity(),"Event Created+1",Toast.LENGTH_LONG).show();
                loader.stopLoading();
                break;
            case Constants.GET_EVENTS:
                mProgress.dismiss();
                if (s == null ) {
                   // mOutputText.setText("No results returned.");
                } else {
                    //output.add(0, "Data retrieved using the Google Calendar API:");
                    //mOutputText.setText(s);
                }
                break;
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgress != null) {
            mProgress.dismiss();
            mProgress = null;
        }
    }
    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            Toast.makeText(getActivity(),"this case",Toast.LENGTH_LONG).show();
            chooseAccount();
        } else if (! isDeviceOnline()) {
            //mOutputText.setText("No network connection available.");
        } else {
            String s=fromDate.getText().toString()+"T"+startTime.getText().toString()+":00+05:30";
            //createEvent(mCredential,s);
            summ=summary.getText().toString();
            desc=description.getText().toString();
            loc=location.getText().toString();
            //new MakeRequestTask(mCredential,s,summ,desc,loc,emailList).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(Constants.REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                getActivity(), new String[]{Manifest.permission.GET_ACCOUNTS})) {
            String accountName = getActivity().getPreferences(Context.MODE_PRIVATE)
                    .getString(Constants.PREF_ACCOUNT_NAME, null);
            accountName=mCredential.getSelectedAccountName();
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
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

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getActivity());
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                getActivity(),
                connectionStatusCode,
                Constants.REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
    public void createEvent(final GoogleAccountCredential mCredential, String startDateTime1, String endDateTime1, String summ, String loca,
                            String desc, List<String> emailList, String frequency, int interval, int count, int notify) throws IOException {

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("R_D_Location Calendar")
                .setHttpRequestInitializer(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) throws IOException {
                        mCredential.initialize(request);
                        request.setConnectTimeout(300000);
                        request.setReadTimeout(300000);
                    }
                })
                .build();
        //Set event location and description
        Event event = new Event()
                .setSummary(summ)//variable
                .setLocation(loca)//variable
                .setDescription(desc);//variable
        Event.ExtendedProperties extendedProperties=new Event.ExtendedProperties();
        Map<String,String> map= new HashMap<>();
        map.put("group",group.getText().toString());
        extendedProperties.setShared(map);;
        event.setExtendedProperties(extendedProperties);
        //set event start and end time
        DateTime startDateTime = new DateTime(startDateTime1);//variable
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Kolkata");//variable
        event.setStart(start);
        if(group.getText().toString().compareTo("nogroup")!=0)
            event.setEtag(group.getText().toString());
        DateTime endDateTime = new DateTime(endDateTime1);//variable
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Kolkata");//variable
        event.setEnd(end);
        //event.setICalUID(group.getText().toString());
        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(notify),//variable
                new EventReminder().setMethod("popup").setMinutes(notify),//variable
                new EventReminder().setMethod("sms").setMinutes(notify)
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);
        String[] recurrence = new String[]{"RRULE:FREQ="+frequency+";COUNT="+count+";INTERVAL="+interval};//variable
        event.setRecurrence(Arrays.asList(recurrence));
        event.setGuestsCanSeeOtherGuests(false);
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String calendarId = "primary";

            // set event attendees
        EventAttendee[] attendees=new EventAttendee[emailList.size()];
        for(int i=0;i<emailList.size();i++) {
            attendees[i] = new EventAttendee().setEmail(emailList.get(i));
            attendees[i].setOrganizer(true);
            if(checkBox.isChecked())
               attendees[i].setResponseStatus("accepted");
        }
        event.setAttendees(Arrays.asList(attendees));
        event.setEtag(group.getText().toString());
        try {
                event = service.events().insert(calendarId, event).setSendNotifications(true).execute();
            }
        catch (UserRecoverableAuthIOException e) {
            startActivityForResult(e.getIntent(), Constants.REQUEST_AUTHORIZATION);
        }
        Map<String,Object> event1=new HashMap<>();
        event1.put("summary",event.getSummary());
        event1.put("location",event.getLocation());
        event1.put("description",event.getDescription());
        event1.put("email",email);
        event1.put("group",group.getText().toString());
        event1.put("id",event.getId());
        event1.put("from",startDateTime1);
        event1.put("to ",endDateTime1);
        event1.put("frequency",frequency);
        event1.put("count",count);
        event1.put("link",event.getHtmlLink());
        event1.put("number_of_students",emailList.size());
        db.collection("events+"+firebaseUser.getEmail())
                .add(event1)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(),"Event added with id "+documentReference.getId(),Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Error adding Event",Toast.LENGTH_LONG).show();
                    }
                });
            System.out.printf("Event created: %s\n", event.getHtmlLink());

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == Constants.PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                name = data.getData().getLastPathSegment();
                uploadFile(data.getData(),name);
                Toast.makeText(getActivity(),name,Toast.LENGTH_LONG).show();
                //openCSV(name,data.getData().getPath());
            }else{
                Toast.makeText(getActivity(), "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void uploadFile(Uri data, final String name1) {
        //progressBar.setVisibility(View.VISIBLE);
        StorageReference sRef = mStorageReference.child(userName+"/" + System.currentTimeMillis() + ".csv");
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       // progressBar.setVisibility(View.GONE);
                        //textViewStatus.setText("File Uploaded Successfully");

                        Upload upload = new Upload(name1, taskSnapshot.getDownloadUrl().toString());
                        mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(upload);
                        //Opening the upload file in browser using the upload url
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(upload.getUrl()));
                        String filename = "CalRem";
                        String fileContents = "Hello world!";
                        final String url=upload.getUrl();
                        final String name=upload.getName();
                        FileOutputStream outputStream;
                        try {
                            outputStream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        new AsyncTask<Void,Void,Void>(){
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                mProgress.setMessage("Please Wait...");
                                mProgress.show();
                            }

                            @Override
                            protected Void doInBackground(Void... voids) {
                                try {
                                    DownloadDatabase(url,name);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                mProgress.dismiss();
                                Toast.makeText(getActivity(),"File Downloaded Sucessfully",Toast.LENGTH_LONG).show();
                                File root = android.os.Environment.getExternalStorageDirectory();
                                emailList=openCSV(name,root.getAbsolutePath() + "/CalRem/files");
                            }
                        }.execute();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        //textViewStatus.setText((int) progress + "% Uploading...");
                    }
                });

    }


    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M&&(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getActivity().getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select the CSV File"), Constants.PICK_PDF_CODE);
    }


    public void DownloadDatabase(String DownloadUrl, String fileName) throws IOException {
        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/CalRem/files");
            if (dir.exists() == false) {
                dir.mkdirs();
            }

            URL url = new URL(DownloadUrl);
            File file = new File(dir, fileName);

            long startTime = System.currentTimeMillis();
            Log.d("DownloadManager", "download url:" + url);
            Log.d("DownloadManager", "download file name:" + fileName);

            URLConnection uconn = url.openConnection();
            uconn.setReadTimeout(10000000);
            uconn.setConnectTimeout(10000000);

            InputStream is = uconn.getInputStream();

            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            //We create an array of bytes
            byte[] data = new byte[50];
            int current = 0;

            while((current = bis.read(data,0,data.length)) != -1){
                buffer.write(data,0,current);
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer.toByteArray());
            fos.flush();
            fos.close();
            Log.d("DownloadManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + "sec");
            //Toast.makeText(DealWithCSV.this,"download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + "sec",Toast.LENGTH_LONG).show();
            int dotindex = fileName.lastIndexOf('.');
            if (dotindex >= 0) {
                fileName = fileName.substring(0, dotindex);

            }
        }
        catch(IOException e) {
            Log.d("DownloadManager" , "Error:" + e);
        }

    }

    public ArrayList<String> openCSV(String name,String path)
    {
        nameOfFile.setText(name);
        ArrayList<String> email1=new ArrayList<>();
        try{
            if(name.contains("primary:csv"))
            {
                name.replace("primary:csv/","");
                Toast.makeText(getActivity(),"here",Toast.LENGTH_LONG).show();
            }
            File csvfile = new File(path+ "/"+name);
            CSVReader reader = new CSVReader(new FileReader(csvfile));
            String [] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                System.out.println(nextLine[0]+"\n");
                if(nextLine[0].compareTo("email")!=0||nextLine[0].compareTo("Email")!=0)
                    email1.add(nextLine[0]);
            }
            emails.setText(android.text.TextUtils.join(",", email1));

        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getActivity(), "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
        return email1;
    }
}
