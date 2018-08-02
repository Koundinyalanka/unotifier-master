package com.remainder.events.unotifier.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.opencsv.CSVReader;
import com.remainder.events.unotifier.BatchEventActivity;
import com.remainder.events.unotifier.Helpers.Constants;
import com.remainder.events.unotifier.Helpers.SqliteDB;
import com.remainder.events.unotifier.Helpers.Upload;
import com.remainder.events.unotifier.R;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    FirebaseFirestore db;
    String userName;
    public int choice;
    private TextView name,email,nameOfFile;
    private ImageView imageView;
    String name_file;
    ProgressDialog mProgress;
    GoogleAccountCredential mCredential;
    private Button upload,cohort,cohort_slack,project;
    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == Constants.PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                name_file = data.getData().getLastPathSegment();
                uploadFile(data.getData(),name_file);
                Toast.makeText(getActivity(),name_file,Toast.LENGTH_LONG).show();
                //openCSV(name,data.getData().getPath());
            }else{
                Toast.makeText(getActivity(), "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        userName=firebaseUser.getDisplayName();

        //getting firebase objects
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(userName);
        if(mCredential!=null)
        {
            Toast.makeText(getActivity(),mCredential.getSelectedAccountName(),Toast.LENGTH_LONG).show();
        }
        mProgress=new ProgressDialog(getActivity());
        imageView=(ImageView)view.findViewById(R.id.profile_image);
        name=(TextView)view.findViewById(R.id.profile_name);
        email=(TextView)view.findViewById(R.id.profile_email);
        upload=(Button) view.findViewById(R.id.upload_csv);
        nameOfFile=(TextView) view.findViewById(R.id.name_of_File);
        cohort=(Button)view.findViewById(R.id.cohort_expiry);
        project=(Button)view.findViewById(R.id.cohort_project);
        Picasso.with(getActivity()).load(firebaseUser.getPhotoUrl()).into(imageView);
        name.setText(firebaseUser.getDisplayName());
        email.setText(firebaseUser.getEmail());
        cohort_slack=(Button)view.findViewById(R.id.cohort_slack);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getActivity(), Arrays.asList(Constants.SCOPES))
                .setBackOff(new ExponentialBackOff());
        mCredential.setSelectedAccountName(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("google", null));
        super.onViewCreated(view, savedInstanceState);
        cohort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(), BatchEventActivity.class);
                i.putExtra("expiry",0);
                startActivity(i);
            }
        });
        project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),BatchEventActivity.class);
                intent.putExtra("project",2);
                startActivity(intent);
            }
        });
        cohort_slack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),BatchEventActivity.class);
                i.putExtra("slack",1);
                startActivity(i);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                CharSequence items[] = new CharSequence[] {"Nanodegree Expiry", "Project Due Dates","Slack Invite Links"};
                adb.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface d, int n) {

                    }

                });
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ListView lv = ((AlertDialog)dialogInterface).getListView();
                        Integer selected = (Integer)lv.getCheckedItemPosition();
                        switch (selected)
                        {
                            case 0:
                                Toast.makeText(getActivity(),"Nanodegree Expiry",Toast.LENGTH_LONG).show();
                                getPDF();
                                choice=0;
                                break;
                            case 1:
                                Toast.makeText(getActivity(),"Project Due Date",Toast.LENGTH_LONG).show();
                                getPDF();
                                choice=1;
                                break;
                            case 2:
                                choice=2;
                                Toast.makeText(getActivity(),"Slack Invite Links",Toast.LENGTH_LONG).show();
                                getPDF();
                                break;
                        }
                    }
                });
                adb.setTitle("Choose your purpose of uploading the csv");
                adb.show();
            }
        });
        //getLoaderManager().initLoader(Constants.GET_EVENTS,null,ProfileFragment.this);
        getLoaderManager().initLoader(Constants.SLACK_DEEPLINK,null,ProfileFragment.this);

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
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        final com.google.api.services.calendar.Calendar mService;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
        final String token="";
        switch (i)
        {
            case Constants.GET_EVENTS:
                return new AsyncTaskLoader<String>(getActivity()) {
                    @Override
                    public String loadInBackground() {
                        List<String> eventList;
                        String result=null;
                        try {
                            eventList=getDataFromApi(mService);
                            result= TextUtils.join("\n",eventList);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return result;
                    }

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        //mProgress.show();
                        forceLoad();
                    }
                };
            case Constants.SLACK_DEEPLINK:
                return new AsyncTaskLoader<String>(getActivity()) {

                    @Override
                    public String loadInBackground() {
                        try {
                            /*InputStream serviceAccount =getResources().openRawResource(R.raw.googleservices);
// Authenticate a Google credential with the service account
                            GoogleCredential googleCred = GoogleCredential.fromStream(serviceAccount);

// Add the required scope to the Google credential
                            GoogleCredential scoped = googleCred.createScoped(
                                    Arrays.asList(
                                            "https://www.googleapis.com/auth/firebase"
                                    )
                            );

// Use the Google credential to generate an access token
                            scoped.refreshToken();
                            String token = scoped.getAccessToken();
                            Log.e("--------->",token);*/

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        return token;
                    }

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }
                };
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {
        switch (loader.getId())
        {
            case Constants.GET_EVENTS:
                break;
            case Constants.SLACK_DEEPLINK:
                Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
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
    private List<String> getDataFromApi(com.google.api.services.calendar.Calendar mService) throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        //Toast.makeText(getActivity(),System.currentTimeMillis()+"",Toast.LENGTH_LONG).show();
        List<String> eventStrings = new ArrayList<String>();
        Events events = mService.events().list("primary")
                .setMaxResults(100)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        List<Event> items = events.getItems();

        for (Event event : items) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                // All-day events don't have start times, so just use
                // the start date.
                start = event.getStart().getDate();
            }
            eventStrings.add(
                    String.format("%s (%s)", event.getSummary(), start));
        }
        return eventStrings;
    }
    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M&&(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) {
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
                            outputStream = getActivity().openFileOutput(filename, MODE_PRIVATE);
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

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                final EditText input = new EditText(getActivity());
                                alertDialog.setTitle("Enter the name of group");
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                input.setLayoutParams(lp);
                                alertDialog.setView(input);
                                alertDialog.setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                SharedPreferences.Editor editor = getActivity().getSharedPreferences("group", MODE_PRIVATE).edit();
                                                editor.putString("name", input.getText().toString());
                                                editor.apply();
                                            }
                                        });
                                alertDialog.show();
                                File root = android.os.Environment.getExternalStorageDirectory();
                                openCSV(name,root.getAbsolutePath() + "/CalRem/files");
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
    public void openCSV(String name,String path)
    {

        SqliteDB sqliteDB=new SqliteDB(getActivity());
        nameOfFile.setText(name);
        ArrayList<String> email1=new ArrayList<>();
        if(choice==0)
        {
            try{
                File csvfile = new File(path+ "/"+name);
                CSVReader reader = new CSVReader(new FileReader(csvfile));
                String [] nextLine;
                ArrayList<String> emailList=new ArrayList<>();
                String nanodegree="",expiryDate="";
                int i=0,temp=0;
                while ((nextLine = reader.readNext()) != null) {
                    // nextLine[] is an array of values from the line
                    if(i==0)
                    {
                        i++;
                        continue;
                    }
                    if(i==1)
                    {
                        temp=Integer.parseInt(nextLine[2]);
                        i++;
                    }
                    if(Integer.parseInt(nextLine[2])==temp) {
                        emailList.add(nextLine[0]);
                        nanodegree=nextLine[1];
                        expiryDate=nextLine[3];
                        //sqliteDB.fillCohortValues();
                    }
                    else {
                        sqliteDB.fillExpiryCohortValues(temp,android.text.TextUtils.join(",",emailList),nanodegree,expiryDate);
                        System.out.println(temp + "\t" + android.text.TextUtils.join(",",emailList) + "\t" + nanodegree + "\t" + expiryDate + "\n");
                        Toast.makeText(getActivity(), "filled cohort "+temp, Toast.LENGTH_LONG).show();
                        emailList.clear();
                        temp=Integer.parseInt(nextLine[2]);
                        emailList.add(nextLine[0]);
                        nanodegree=nextLine[1];
                        expiryDate=nextLine[3];
                    }
                }
                //emails.setText(android.text.TextUtils.join(",", email1));

            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(getActivity(), "The specified file was not found"+path, Toast.LENGTH_SHORT).show();
            }
        }
        else if(choice==1)
        {
            try{
                File csvfile = new File(path+ "/"+name);
                CSVReader reader = new CSVReader(new FileReader(csvfile));
                String [] nextLine;
                String p="";
                String projectName="";
                ArrayList<String> emailList=new ArrayList<>();
                String nanodegree="",expiryDate="";
                int i=0,temp=0;
                int j=0;
                while ((nextLine = reader.readNext()) != null) {
                    // nextLine[] is an array of values from the line
                    if(i==0)
                    {
                        i++;
                        continue;
                    }
                    if(i==1)
                    {

                        temp=Integer.parseInt(nextLine[1]);
                        p=nextLine[4];
                        nanodegree = nextLine[2];
                        projectName = nextLine[3];
                        expiryDate = nextLine[4];
                        i++;
                    }

                    if(Integer.parseInt(nextLine[1])==temp&&p.compareTo(nextLine[4])==0) {
                        while (Integer.parseInt(nextLine[1])==temp&&p.compareTo(nextLine[4])==0) {
                            emailList.add(nextLine[0]);
                            nanodegree = nextLine[2];
                            projectName = nextLine[3];
                            expiryDate = nextLine[4];
                            nextLine=reader.readNext();
                        }
                        //sqliteDB.fillCohortValues();
                    }
                    else {
                        sqliteDB.fillProjectCohortValues(temp,android.text.TextUtils.join(",",emailList),nanodegree,expiryDate,projectName);
                        System.out.println(temp + "\t" + android.text.TextUtils.join(",",emailList) + "\t" + nanodegree + "\t" + expiryDate + "\n");
                        Toast.makeText(getActivity(), "filled cohort "+projectName, Toast.LENGTH_LONG).show();
                        emailList.clear();
                        temp=Integer.parseInt(nextLine[1]);
                        emailList.add(nextLine[0]);
                        nanodegree=nextLine[2];
                        projectName=nextLine[3];
                        p=nextLine[4];
                        expiryDate=nextLine[4];j=0;
                        while (Integer.parseInt(nextLine[1])==temp&&p.compareTo(nextLine[4])==0) {
                            emailList.add(nextLine[0]);
                            nanodegree = nextLine[2];
                            projectName = nextLine[3];
                            expiryDate = nextLine[4];
                            j++;
                            nextLine=reader.readNext();
                            if(j>50)
                            {
                                j=0;
                                break;
                            }
                        }
                    }
                }
                //emails.setText(android.text.TextUtils.join(",", email1));

            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(getActivity(), "The specified file was not found"+path+e.toString(), Toast.LENGTH_LONG).show();
            }
        }

        else if(choice==2)
        {
            try{
                File csvfile = new File(path+ "/"+name);
                CSVReader reader = new CSVReader(new FileReader(csvfile));
                String [] nextLine;
                ArrayList<String> emailList=new ArrayList<>();
                String nanodegree="",slackInvite="",slackLink="";
                int i=0,temp=0;
                while ((nextLine = reader.readNext()) != null) {
                    // nextLine[] is an array of values from the line
                    if(i==0)
                    {
                        i++;
                        continue;
                    }
                    if(i==1)
                    {
                        temp=Integer.parseInt(nextLine[2]);
                        i++;
                    }
                    if(Integer.parseInt(nextLine[2])==temp) {
                        emailList.add(nextLine[0]);
                        nanodegree=nextLine[1];
                        slackLink=nextLine[3];
                        slackInvite=nextLine[4];
                        //sqliteDB.fillCohortValues();
                    }
                    else {
                        sqliteDB.fillSlackCohortValues(temp,android.text.TextUtils.join(",",emailList),nanodegree,slackLink,slackInvite);
                        System.out.println(temp + "\t" + android.text.TextUtils.join(",",emailList) + "\t" + nanodegree + "\t" + slackInvite+ "\n");
                        Toast.makeText(getActivity(), "filled cohort "+temp, Toast.LENGTH_LONG).show();
                        emailList.clear();
                        temp=Integer.parseInt(nextLine[2]);
                        emailList.add(nextLine[0]);
                        nanodegree=nextLine[1];
                        slackLink=nextLine[3];
                        slackInvite=nextLine[4];
                    }
                }
                //emails.setText(android.text.TextUtils.join(",", email1));

            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(getActivity(),"The specified file was not found", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
