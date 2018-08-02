package com.remainder.events.unotifier.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class SqliteDB extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME="Cohorts";
    public static final String TABLE_NAME_COHORTS_EXPIRY="CohortsExpiry";
    public static final String TABLE_NAME_COHORTS_SLACK="CohortsSlack";
    public static final String TABLE_NAME_COHORTS_PROJECT="CohortsProject";
    public static final String COLUMN_NAME_COHORTID="CohortID";
    public static final String COLUMN_NAME_EMAIL="Email";
    public static final String COLUMN_NAME_NANODEGREE="Nanodegree";
    public static final String COLUMN_NAME_EXPIRY_DATE="Date";
    public static final String COLUMN_NAME_SLACK_INVITE="SlackInviteLink";
    public static final String COLUMN_NAME_SLACK_LINK="SlackLink";
    public static final String COLUMN_NAME_PROJECT="ProjectName";
    public SqliteDB(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_EXPIRY="CREATE TABLE "+ TABLE_NAME_COHORTS_EXPIRY+"("
                +COLUMN_NAME_COHORTID +" INTEGER"+","
                + COLUMN_NAME_EMAIL+" TEXT"
                +","+COLUMN_NAME_NANODEGREE +" TEXT"+","
                + COLUMN_NAME_EXPIRY_DATE+" TEXT"+")";
        sqLiteDatabase.execSQL(CREATE_TABLE_EXPIRY);
        String CREATE_TABLE_SLACK="CREATE TABLE "+ TABLE_NAME_COHORTS_SLACK+"("
                +COLUMN_NAME_COHORTID +" INTEGER"+","
                + COLUMN_NAME_EMAIL+" TEXT"+","+COLUMN_NAME_NANODEGREE +" TEXT"+","
                + COLUMN_NAME_SLACK_INVITE+" TEXT"+","+
                COLUMN_NAME_SLACK_LINK +" TEXT"+")";
        sqLiteDatabase.execSQL(CREATE_TABLE_SLACK);
        String CREATE_TABLE_PROJECT="CREATE TABLE "+ TABLE_NAME_COHORTS_PROJECT+"("
                +COLUMN_NAME_COHORTID +" INTEGER"+","
                + COLUMN_NAME_EMAIL+" TEXT"
                +","+COLUMN_NAME_NANODEGREE +" TEXT"+","
                +COLUMN_NAME_PROJECT +" TEXT"+","
                + COLUMN_NAME_EXPIRY_DATE+" TEXT"+")";
        sqLiteDatabase.execSQL(CREATE_TABLE_PROJECT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void fillExpiryCohortValues(int cohortid,String email,String nanodegree,String expiryDate)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_COHORTID, cohortid);
        values.put(COLUMN_NAME_EMAIL, email);
        values.put(COLUMN_NAME_NANODEGREE, nanodegree);
        values.put(COLUMN_NAME_EXPIRY_DATE,expiryDate);
        db.insert(TABLE_NAME_COHORTS_EXPIRY, null, values);
        db.close();
        Log.e("Cohort", "filled");
    }
    public ArrayList<CohortExpiry> getExpiryCohortValues()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor=null;
        ArrayList<CohortExpiry> cohortArrayList=new ArrayList<>();
        try {
            String QUERY="SELECT * FROM "+TABLE_NAME_COHORTS_EXPIRY;
            cursor=db.rawQuery(QUERY,null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                cohortArrayList.add(new CohortExpiry(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)));
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return cohortArrayList;
    }

    public void deleteExpiryCohortValues(int cohortid)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String QUERY="SELECT * FROM "+TABLE_NAME_COHORTS_EXPIRY+" WHERE "+COLUMN_NAME_COHORTID+" = "+cohortid;
        db.execSQL(QUERY);
    }
    public void fillSlackCohortValues(int cohortid,String email,String nanodegree,String slackLink,String slackInvite)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_COHORTID, cohortid);
        values.put(COLUMN_NAME_EMAIL, email);
        values.put(COLUMN_NAME_NANODEGREE, nanodegree);
        values.put(COLUMN_NAME_SLACK_LINK,slackLink);
        values.put(COLUMN_NAME_SLACK_INVITE,slackInvite);
        db.insert(TABLE_NAME_COHORTS_SLACK, null, values);
        db.close();
        Log.e("Cohort Slack", "filled");
    }
    public ArrayList<CohortSlack> getSlackCohortValues()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor=null;
        ArrayList<CohortSlack> cohortArrayList=new ArrayList<>();
        try {
            String QUERY="SELECT * FROM "+TABLE_NAME_COHORTS_SLACK;
            cursor=db.rawQuery(QUERY,null);
            cursor.moveToFirst();
            int i=0;
            while(!cursor.isAfterLast())
            {
                if(i==0)
                {
                    i++;
                    continue;
                }
//                Log.e("------->",String.valueOf(cursor.getString(5)));
                cohortArrayList.add(new CohortSlack(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)));
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return cohortArrayList;
    }

    public void deleteSlackCohortValues(int cohortid)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String QUERY="SELECT * FROM "+TABLE_NAME_COHORTS_SLACK+" WHERE "+COLUMN_NAME_COHORTID+" = "+cohortid;
        db.execSQL(QUERY);
    }
    public void fillProjectCohortValues(int cohortid,String email,String nanodegree,String expiryDate,String projectName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_COHORTID, cohortid);
        values.put(COLUMN_NAME_EMAIL, email);
        values.put(COLUMN_NAME_NANODEGREE, nanodegree);
        values.put(COLUMN_NAME_PROJECT, projectName);
        values.put(COLUMN_NAME_EXPIRY_DATE,expiryDate);
        db.insert(TABLE_NAME_COHORTS_PROJECT, null, values);
        db.close();
        Log.e("Cohort", "filled");
    }
    public ArrayList<CohortProject> getProjectCohortValues()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor=null;
        ArrayList<CohortProject> cohortArrayList=new ArrayList<>();
        try {
            String QUERY="SELECT * FROM "+TABLE_NAME_COHORTS_PROJECT;
            cursor=db.rawQuery(QUERY,null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                cohortArrayList.add(new CohortProject(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)));
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return cohortArrayList;
    }


    public void deleteProjectCohortValues(int cohortid)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String QUERY="SELECT * FROM "+TABLE_NAME_COHORTS_PROJECT+" WHERE "+COLUMN_NAME_COHORTID+" = "+cohortid;
        db.execSQL(QUERY);
    }
}
