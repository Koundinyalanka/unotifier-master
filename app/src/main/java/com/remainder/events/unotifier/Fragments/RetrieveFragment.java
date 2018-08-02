package com.remainder.events.unotifier.Fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.model.Events;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.remainder.events.unotifier.Adapters.EventAdapter;
import com.remainder.events.unotifier.Helpers.Constants;
import com.remainder.events.unotifier.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;
import static java.lang.Integer.parseInt;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RetrieveFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RetrieveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RetrieveFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    GoogleAccountCredential mCredential;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView recyclerView;
    Button button;
    EditText from;
    String userName;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ProgressDialog progressDialog;
    FirebaseFirestore db;
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    FirebaseUser firebaseUser;
    ArrayList<com.google.api.services.calendar.model.Event> eventArrayList=new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    public RetrieveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RetrieveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RetrieveFragment newInstance(String param1, String param2) {
        RetrieveFragment fragment = new RetrieveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //new DownloadTask().execute();

        mCredential = GoogleAccountCredential.usingOAuth2(
                getActivity(), Arrays.asList(Constants.SCOPES))
                .setBackOff(new ExponentialBackOff());
        progressDialog=new ProgressDialog(getActivity());
        mCredential.setSelectedAccountName(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("google", null));
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
           }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_retrieve, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button=(Button) view.findViewById(R.id.button_events);
        from=(EditText)view.findViewById(R.id.from);
        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userName=firebaseUser.getDisplayName();

        //getting firebase objects
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(userName);
        db=FirebaseFirestore.getInstance();
        if(mCredential!=null)
        {
            Toast.makeText(getActivity(),mCredential.getSelectedAccountName(),Toast.LENGTH_LONG).show();
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putInt("from",parseInt(from.getText().toString()));
                getLoaderManager().initLoader(Constants.GET_EVENTS,bundle,RetrieveFragment.this);
                recyclerView.setHasFixedSize(true);
            }
        });
        //getLoaderManager().initLoader(GET_EVENTS,null,RetrieveFragment.this);


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
   


    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        final com.google.api.services.calendar.Calendar mService;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
        final int from=bundle.getInt("from");
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        switch (i) {
            case Constants.GET_EVENTS:
                return new AsyncTaskLoader<String>(getActivity()) {
                    @Override
                    public String loadInBackground() {
                        List<String> eventList;
                        String result = null;
                        try {
                            eventList = getDataFromApi(mService,from);
                            result = TextUtils.join("\n", eventList);
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
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {
        switch (loader.getId())
        {
            case Constants.GET_EVENTS:
                //eventsList.setText(s);
                progressDialog.cancel();
                //Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(1);
                EventAdapter eventAdapter=new EventAdapter(getActivity(),eventArrayList);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(eventAdapter);
                break;

        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
    private List<String> getDataFromApi(com.google.api.services.calendar.Calendar mService,int from) throws IOException {
        // List the next 10 events from the primary calendar.
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH,0-from);
        Date date=calendar.getTime();
        DateTime now = new DateTime(date);
        //Toast.makeText(getActivity(),System.currentTimeMillis()+"",Toast.LENGTH_LONG).show();
        List<String> eventStrings = new ArrayList<String>();
        Events events = mService.events().list("primary")
                .setMaxResults(100)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        CollectionReference collectionReference=db.collection("events+"+firebaseUser.getEmail());

        List<com.google.api.services.calendar.model.Event> items = events.getItems();
        int i=0;
        for (com.google.api.services.calendar.model.Event event : items) {
            DateTime start = event.getStart().getDateTime();
            eventArrayList.add(i,event);
            if (start == null) {
                // All-day events don't have start times, so just use
                // the start date.
                start = event.getStart().getDate();

            }
            i++;
            //Toast.makeText(getActivity(),eventArrayList.get(i).getSummary(),Toast.LENGTH_LONG).show();

            eventStrings.add(
                    String.format("%s (%s)", event.getSummary(), start));
            Log.e("------->",event.getSummary());
        }
        return eventStrings;
    }
}
