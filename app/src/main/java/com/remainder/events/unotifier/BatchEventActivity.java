package com.remainder.events.unotifier;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.remainder.events.unotifier.Adapters.CohortExpiryAdapter;
import com.remainder.events.unotifier.Adapters.CohortProjectAdapter;
import com.remainder.events.unotifier.Adapters.CohortSlackAdapter;
import com.remainder.events.unotifier.Helpers.CohortExpiry;
import com.remainder.events.unotifier.Helpers.CohortProject;
import com.remainder.events.unotifier.Helpers.CohortSlack;
import com.remainder.events.unotifier.Helpers.SqliteDB;
import com.remainder.events.unotifier.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BatchEventActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SqliteDB sqliteDB;
    ArrayList<CohortExpiry> cohortList=new ArrayList<>();
    ArrayList<CohortSlack> cohortSlacks=new ArrayList<>();
    ArrayList<CohortProject> cohortProjects=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqliteDB=new SqliteDB(this);
        setContentView(R.layout.activity_batch_event);
        Intent intent=getIntent();
        if(intent.hasExtra("expiry")){
            if (intent.getIntExtra("expiry", 1) == 0) {
        cohortList=sqliteDB.getExpiryCohortValues();
        for(int i=0;i<cohortList.size();i++) {
                CohortExpiry cohort = cohortList.get(i);
                String[] token = cohort.getExpiryDate().split("/");
                int date = Integer.parseInt(token[1]);
                int month = Integer.parseInt(token[0]);
                int year = Integer.parseInt(token[2]);
                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);

                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = df.format(c);
                String[] token1 = formattedDate.split("-");
                int date1 = Integer.parseInt(token1[0]);
                int month1 = Integer.parseInt(token1[1]);
                int year1 = Integer.parseInt(token1[2]);
                if (date1 > date && month1 >= month && year1 >= year) {
                    sqliteDB.deleteExpiryCohortValues(cohort.getCohortId());
                }
            }
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BatchEventActivity.this);
            linearLayoutManager.setOrientation(1);
            recyclerView.setLayoutManager(linearLayoutManager);
            CohortExpiryAdapter cohortAdapter = new CohortExpiryAdapter(this, sqliteDB.getExpiryCohortValues());
            recyclerView.setAdapter(cohortAdapter);
        }
        }
        else if(intent.hasExtra("slack"))
        {
            if(intent.getIntExtra("slack",0)==1) {
                cohortSlacks = sqliteDB.getSlackCohortValues();
                /*for (int i = 0; i < cohortSlacks.size(); i++) {
                    CohortSlack cohort = cohortSlacks.get(i);
                    String[] token = cohort.getExpiryDate().split("/");
                    int date = Integer.parseInt(token[1]);
                    int month = Integer.parseInt(token[0]);
                    int year = Integer.parseInt(token[2]);
                    Date c = Calendar.getInstance().getTime();
                    System.out.println("Current time => " + c);

                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    String formattedDate = df.format(c);
                    String[] token1 = formattedDate.split("-");
                    int date1 = Integer.parseInt(token1[0]);
                    int month1 = Integer.parseInt(token1[1]);
                    int year1 = Integer.parseInt(token1[2]);
                    if (date1 > date && month1 >= month && year1 >= year) {
                        sqliteDB.deleteExpiryCohortValues(cohort.getCohortId());
                    }
                }*/
                recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BatchEventActivity.this);
                linearLayoutManager.setOrientation(1);
                recyclerView.setLayoutManager(linearLayoutManager);
                CohortSlackAdapter cohortAdapter = new CohortSlackAdapter(this, sqliteDB.getSlackCohortValues());
                recyclerView.setAdapter(cohortAdapter);
            }
        }
        else if(intent.hasExtra("project"))
        {
            if(intent.getIntExtra("project",0)==2)
            {
                cohortProjects=sqliteDB.getProjectCohortValues();
                recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BatchEventActivity.this);
                linearLayoutManager.setOrientation(1);
                recyclerView.setLayoutManager(linearLayoutManager);
                CohortProjectAdapter cohortProjectAdapter=new CohortProjectAdapter(this,sqliteDB.getProjectCohortValues());
                recyclerView.setAdapter(cohortProjectAdapter);
            }
        }

    }
}
