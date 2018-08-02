package com.remainder.events.unotifier.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

public class CohortSlack implements Parcelable {
    int cohortId;
    String emailList,nanodegree,slackLink,slackInvite;
    public CohortSlack()
    {

    }
    public CohortSlack(int cohortId, String emailList, String nanodegree,String slackLink,String slackInvite)
    {
        this.cohortId=cohortId;
        this.emailList=emailList;
        this.nanodegree=nanodegree;
        this.slackLink=slackLink;
        this.slackInvite=slackInvite;
    }


    protected CohortSlack(Parcel in) {
        cohortId = in.readInt();
        emailList = in.readString();
        nanodegree = in.readString();
        slackLink = in.readString();
        slackInvite = in.readString();
    }

    public static final Creator<CohortSlack> CREATOR = new Creator<CohortSlack>() {
        @Override
        public CohortSlack createFromParcel(Parcel in) {
            return new CohortSlack(in);
        }

        @Override
        public CohortSlack[] newArray(int size) {
            return new CohortSlack[size];
        }
    };

    public int getCohortId() {
        return cohortId;
    }

    public String getEmailList() {
        return emailList;
    }


    public String getNanodegree() {
        return nanodegree;
    }

    public void setEmailList(String emailList) {
        this.emailList = emailList;
    }

    public void setCohortId(int cohortId) {
        this.cohortId = cohortId;
    }


    public void setNanodegree(String nanodegree) {
        this.nanodegree = nanodegree;
    }

    public String getSlackInvite() {
        return slackInvite;
    }

    public void setSlackInvite(String slackInvite) {
        this.slackInvite = slackInvite;
    }

    public String getSlackLink() {
        return slackLink;
    }

    public void setSlackLink(String slackLink) {
        this.slackLink = slackLink;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(cohortId);
        parcel.writeString(emailList);
        parcel.writeString(nanodegree);
        parcel.writeString(slackLink);
        parcel.writeString(slackInvite);
    }
}
