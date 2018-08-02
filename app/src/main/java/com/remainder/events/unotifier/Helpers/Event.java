package com.remainder.events.unotifier.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Event implements Parcelable {
    String startDateTime1;
    String endDateTime1;
    String summ;
    String loca;
    String desc;
    String emailList;
    String frequency;
    String link;
    ArrayList<String> accepted;
    ArrayList<String> tenative;
    ArrayList<String> declined;
    ArrayList<String> needsAction;
    int interval,count,numos;
    public Event(String startDateTime1, String endDateTime1, String summ, String loca,
                 String desc, String emailList, String frequency, int interval, int count,int numos,String link,
                 ArrayList<String> accepted,ArrayList<String> tenative,ArrayList<String> declined,ArrayList<String> needsAction)
    {
        this.startDateTime1=startDateTime1;
        this.endDateTime1=endDateTime1;
        this.summ=summ;
        this.loca=loca;
        this.desc=desc;
        this.emailList=emailList;
        this.frequency=frequency;
        this.interval=interval;
        this.count=count;
        this.numos=numos;
        this.link=link;
        this.accepted=accepted;
        this.tenative=tenative;
        this.declined=declined;
        this.needsAction=needsAction;
    }
    public Event()
    {

    }


    protected Event(Parcel in) {
        startDateTime1 = in.readString();
        endDateTime1 = in.readString();
        summ = in.readString();
        loca = in.readString();
        desc = in.readString();
        emailList = in.readString();
        frequency = in.readString();
        link = in.readString();
        accepted = in.createStringArrayList();
        tenative = in.createStringArrayList();
        declined = in.createStringArrayList();
        needsAction = in.createStringArrayList();
        interval = in.readInt();
        count = in.readInt();
        numos = in.readInt();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public ArrayList<String> getAccepted() {
        return accepted;
    }

    public ArrayList<String> getTenative() {
        return tenative;
    }

    public ArrayList<String> getDeclined() {
        return declined;
    }

    public ArrayList<String> getNeedsAction() {
        return needsAction;
    }

    public void setAccepted(ArrayList<String> accepted) {
        this.accepted = accepted;
    }

    public void setDeclined(ArrayList<String> declined) {
        this.declined = declined;
    }

    public void setTenative(ArrayList<String> tenative) {
        this.tenative = tenative;
    }

    public void setNeedsAction(ArrayList<String> needsAction) {
        this.needsAction = needsAction;
    }

    public int getCount() {
        return count;
    }

    public int getInterval() {
        return interval;
    }

    public String getDesc() {
        return desc;
    }

    public String getEmailList() {
        return emailList;
    }

    public String getEndDateTime1() {
        return endDateTime1;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getLoca() {
        return loca;
    }

    public String getStartDateTime1() {
        return startDateTime1;
    }

    public String getSumm() {
        return summ;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setEndDateTime1(String endDateTime1) {
        this.endDateTime1 = endDateTime1;
    }

    public void setEmailList(String emailList) {
        this.emailList = emailList;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setLoca(String loca) {
        this.loca = loca;
    }

    public void setStartDateTime1(String startDateTime1) {
        this.startDateTime1 = startDateTime1;
    }

    public void setSumm(String summ) {
        this.summ = summ;
    }

    public int getNumos() {
        return numos;
    }

    public void setNumos(int numos) {
        this.numos = numos;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(startDateTime1);
        parcel.writeString(endDateTime1);
        parcel.writeString(summ);
        parcel.writeString(loca);
        parcel.writeString(desc);
        parcel.writeString(emailList);
        parcel.writeString(frequency);
        parcel.writeString(link);
        parcel.writeStringList(accepted);
        parcel.writeStringList(tenative);
        parcel.writeStringList(declined);
        parcel.writeStringList(needsAction);
        parcel.writeInt(interval);
        parcel.writeInt(count);
        parcel.writeInt(numos);
    }


}
