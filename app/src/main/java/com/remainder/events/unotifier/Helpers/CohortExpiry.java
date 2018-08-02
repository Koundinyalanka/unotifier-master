package com.remainder.events.unotifier.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

public class CohortExpiry implements Parcelable {
    int cohortId;
    String emailList,nanodegree,expiryDate;
    public CohortExpiry()
    {

    }
    public CohortExpiry(int cohortId,String emailList,String nanodegree,String expiryDate)
    {
        this.cohortId=cohortId;
        this.emailList=emailList;
        this.nanodegree=nanodegree;
        this.expiryDate=expiryDate;
    }

    protected CohortExpiry(Parcel in) {
        cohortId = in.readInt();
        emailList = in.readString();
        nanodegree = in.readString();
        expiryDate = in.readString();
    }

    public static final Creator<CohortExpiry> CREATOR = new Creator<CohortExpiry>() {
        @Override
        public CohortExpiry createFromParcel(Parcel in) {
            return new CohortExpiry(in);
        }

        @Override
        public CohortExpiry[] newArray(int size) {
            return new CohortExpiry[size];
        }
    };

    public int getCohortId() {
        return cohortId;
    }

    public String getEmailList() {
        return emailList;
    }

    public String getExpiryDate() {
        return expiryDate;
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

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setNanodegree(String nanodegree) {
        this.nanodegree = nanodegree;
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
        parcel.writeString(expiryDate);
    }
}
