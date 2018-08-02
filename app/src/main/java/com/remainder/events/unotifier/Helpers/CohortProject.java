package com.remainder.events.unotifier.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

public class CohortProject implements Parcelable {
    int cohortId;
    String emailList,nanodegree,expiryDate,projectName;
    public CohortProject()
    {

    }
    public CohortProject(int cohortId, String emailList, String nanodegree, String expiryDate,String projectName)
    {
        this.cohortId=cohortId;
        this.emailList=emailList;
        this.nanodegree=nanodegree;
        this.expiryDate=expiryDate;
        this.projectName=projectName;
    }


    protected CohortProject(Parcel in) {
        cohortId = in.readInt();
        emailList = in.readString();
        nanodegree = in.readString();
        expiryDate = in.readString();
        projectName = in.readString();
    }

    public static final Creator<CohortProject> CREATOR = new Creator<CohortProject>() {
        @Override
        public CohortProject createFromParcel(Parcel in) {
            return new CohortProject(in);
        }

        @Override
        public CohortProject[] newArray(int size) {
            return new CohortProject[size];
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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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
        parcel.writeString(projectName);
    }
}
