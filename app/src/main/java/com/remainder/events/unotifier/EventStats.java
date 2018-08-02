package com.remainder.events.unotifier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.remainder.events.unotifier.R;

import java.util.ArrayList;
import java.util.List;

public class EventStats extends AppCompatActivity {
ArrayList<Event> eventArrayList=new ArrayList<>();
List<EventAttendee> list=new ArrayList<>();
    com.remainder.events.unotifier.Helpers.Event event=new com.remainder.events.unotifier.Helpers.Event();
    TextView summ,start_d,start_t,end_d,end_t,loc,des,lin,acc1,dec1,nee1,ten1;
    TextView accepted,declined,needs,tenatively;
    ArrayList<String> acceptedL=new ArrayList<>();
    ArrayList<String> declinedL=new ArrayList<>();
    ArrayList<String> needsL=new ArrayList<>();
    ArrayList<String> tenativelyL=new ArrayList<>();
    int acc=0,dec=0,ten=0,need=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_stats);
        acc1=(TextView)findViewById(R.id.acc);
        dec1=(TextView)findViewById(R.id.dec);
        nee1=(TextView)findViewById(R.id.nee);
        ten1=(TextView)findViewById(R.id.ten);
        summ=(TextView)findViewById(R.id.summary_stat);
        start_d=(TextView)findViewById(R.id.start_date_stat);
        start_t=(TextView)findViewById(R.id.start_time_stat);
        end_d=(TextView)findViewById(R.id.end_date_stat);
        end_t=(TextView)findViewById(R.id.end_time_stat);
        loc=(TextView)findViewById(R.id.location_stat);
        des=(TextView)findViewById(R.id.description_stat);
        lin=(TextView)findViewById(R.id.link_stat);
        accepted=(TextView) findViewById(R.id.accepted_list);
        declined=(TextView) findViewById(R.id.declined_list);
        needs=(TextView) findViewById(R.id.needsAction_list);
        tenatively=(TextView) findViewById(R.id.tenatively_accepted_list);
        Intent intent=getIntent();
        if(intent.hasExtra("event"))
        {
            event=intent.getParcelableExtra("event");
            summ.setText(event.getSumm());
            String date1=event.getStartDateTime1().substring(0,10);
            String time1=event.getStartDateTime1().substring(11,16);
            String date2=event.getEndDateTime1().substring(0,10);
            String time2=event.getEndDateTime1().substring(11,16);
            start_d.setText(date1);
            start_t.setText(time1);
            end_t.setText(time2);
            end_d.setText(date2);
            loc.setText(event.getLoca());
            des.setText(event.getDesc());
            lin.setText(event.getLink());
            acceptedL=event.getAccepted();
            declinedL=event.getDeclined();
            needsL=event.getNeedsAction();
            tenativelyL=event.getTenative();
            acc1.append("  "+acceptedL.size());
            dec1.append("  "+declinedL.size());
            ten1.append("  "+tenativelyL.size());
            nee1.append("  "+needsL.size());
            for(int i=0;i<acceptedL.size();i++)
            {
                accepted.append(acceptedL.get(i)+"\n");
            }
            for(int i=0;i<declinedL.size();i++)
            {
                declined.append(declinedL.get(i)+"\n");
            }
            for(int i=0;i<needsL.size();i++)
            {
                needs.append(needsL.get(i)+"\n");
            }
            for(int i=0;i<tenativelyL.size();i++)
            {
                tenatively.append(tenativelyL.get(i)+"\n");
            }
                //Log.e("--------->",list.get(0).getClass().getName());
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.eventstat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_duplicate:
                Intent intent=new Intent(EventStats.this,EventActivity.class);
                intent.putExtra("duplicate",event);
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);

    }
}


