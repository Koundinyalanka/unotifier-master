package com.remainder.events.unotifier.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.remainder.events.unotifier.EventActivity;
import com.remainder.events.unotifier.EventStats;
import com.remainder.events.unotifier.R;
import com.remainder.events.unotifier.Helpers.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {
    Context context;
    ArrayList<Event> eventArrayList=new ArrayList<>();
    GoogleAccountCredential mCredential;
    public EventAdapter(Context context,ArrayList<Event> eventArrayList)
    {
        this.context=context;
        this.eventArrayList=eventArrayList;
    }
    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        LayoutInflater inflater=LayoutInflater.from(context);
        View v=inflater.inflate(R.layout.event_card,parent,false);
        mCredential = GoogleAccountCredential.usingOAuth2(
                context, Arrays.asList(Constants.SCOPES))
                .setBackOff(new ExponentialBackOff());
        mCredential.setSelectedAccountName(PreferenceManager.getDefaultSharedPreferences(context).getString("google", null));
        EventHolder eventHolder=new EventHolder(v);
        return eventHolder;
    }

    @Override
    public void onBindViewHolder(final EventHolder holder, final int position) {
        holder.bind(position);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,EventStats.class);
                Bundle bundle=new Bundle();
                List<EventAttendee> list=new ArrayList<>();
                list=eventArrayList.get(position).getAttendees();
                Log.e("--------->",list.size()+"");
                ArrayList<String> acceptedL=new ArrayList<>();
                ArrayList<String> declinedL=new ArrayList<>();
                ArrayList<String> needsL=new ArrayList<>();
                ArrayList<String> tenativelyL=new ArrayList<>();
                try {

                    for (int i = 0; i < list.size(); i++) {
                        //Toast.makeText(EventStats.this, event.getAttendeeList().get(i).getClass().getName(), Toast.LENGTH_LONG).show();
                        if (list.get(i).getResponseStatus().toString().compareTo("accepted") == 0) {
                            acceptedL.add(list.get(i).getEmail());
                        }
                        else if (list.get(i).getResponseStatus().toString().compareTo("tentative") == 0) {
                            tenativelyL.add(list.get(i).getEmail());
                        }
                        else if (list.get(i).getResponseStatus().toString().compareTo("declined") == 0) {
                            declinedL.add(list.get(i).getEmail());
                        }
                        else if (list.get(i).getResponseStatus().toString().compareTo("needsAction") == 0) {
                            needsL.add(list.get(i).getEmail());
                        }
                    }
                }
                catch (Exception e)
                {

                }
                intent.putExtra("event",new com.remainder.events.unotifier.Helpers.Event(eventArrayList.get(position).getStart().getDateTime().toString()
                        , eventArrayList.get(position).getEnd().getDateTime().toString(),eventArrayList.get(position).getSummary(),eventArrayList.get(position).getLocation(),
                        eventArrayList.get(position).getDescription(),"emails", "", 0, 0,eventArrayList.get(position).getAttendees().size(),
                        eventArrayList.get(position).getHtmlLink(),acceptedL,tenativelyL,declinedL,needsL));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventArrayList.size();
    }
    public class EventHolder extends RecyclerView.ViewHolder
    {
        TextView summary,date,noofstu;
        CardView cardView;
        ImageButton imageButton;
        public EventHolder(View itemView) {
            super(itemView);
            summary=(TextView)itemView.findViewById(R.id.summary_card);
            date=(TextView)itemView.findViewById(R.id.date_card);
            noofstu=(TextView)itemView.findViewById(R.id.number_card);
            cardView=(CardView)itemView.findViewById(R.id.card_view);
            imageButton=(ImageButton)itemView.findViewById(R.id.delete_event);
        }

        public void bind(final int id)
        {
            summary.setText(eventArrayList.get(id).getSummary()+"  ");
            if(!(eventArrayList.get(id).getExtendedProperties()==null))
            {
                summary.append(eventArrayList.get(id).getExtendedProperties().getShared().get("group")+"");

                if(eventArrayList.get(id).getExtendedProperties().getShared().get("group").equals("")||
                        eventArrayList.get(id).getExtendedProperties().getShared().get("group").equals("nogroup"))
                {
                    if (eventArrayList.get(id).getStart().getDateTime() != null) {
                        String s = eventArrayList.get(id).getStart().getDateTime().toString();
                        String date1 = s.substring(0, 10);
                        String time1 = s.substring(11, 16);
                        date.setText(date1 + "      " + time1);
                    }
                    if (eventArrayList.get(id).getAttendees() != null)
                        noofstu.setText(eventArrayList.get(id).getAttendees().size() + "");
                    else
                        noofstu.setText(0 + "");

                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("StaticFieldLeak")
                        @Override
                        public void onClick(View view) {
                            new AsyncTask<Void, Void, Void>() {

                                @Override
                                protected Void doInBackground(Void... voids) {
                                    HttpTransport transport = AndroidHttp.newCompatibleTransport();
                                    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                                    Calendar service = new Calendar.Builder(transport, jsonFactory, mCredential)
                                            .setApplicationName("applicationName").build();
                                    // Delete an event
                                    try {
                                        service.events().delete("primary", eventArrayList.get(id).getId()).execute();
                                        Log.e("event id--------->", eventArrayList.get(id).getId());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                            }.execute();
                        }
                    });
                }
                else
                {
                    summary.setText(eventArrayList.get(id).getExtendedProperties().getShared().get("group"));
                }
            }
            else {
                if (eventArrayList.get(id).getStart().getDateTime() != null) {
                    String s = eventArrayList.get(id).getStart().getDateTime().toString();
                    String date1 = s.substring(0, 10);
                    String time1 = s.substring(11, 16);
                    date.setText(date1 + "      " + time1);
                }
                if (eventArrayList.get(id).getAttendees() != null)
                    noofstu.setText(eventArrayList.get(id).getAttendees().size() + "");
                else
                    noofstu.setText(0 + "");

                imageButton.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onClick(View view) {
                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... voids) {
                                HttpTransport transport = AndroidHttp.newCompatibleTransport();
                                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                                Calendar service = new Calendar.Builder(transport, jsonFactory, mCredential)
                                        .setApplicationName("applicationName").build();
                                // Delete an event
                                try {
                                    service.events().delete("primary", eventArrayList.get(id).getId()).execute();
                                    Log.e("event id--------->", eventArrayList.get(id).getId());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        }.execute();
                    }
                });
            }
        }
    }
}