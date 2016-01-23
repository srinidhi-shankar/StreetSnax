package com.streetsnax.srinidhi.streetsnax.utilities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by I15230 on 12/31/2015.
 */

public class SearchBundleData implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SearchBundleData createFromParcel(Parcel in) {
            return new SearchBundleData(in);
        }

        public SearchBundleData[] newArray(int size) {
            return new SearchBundleData[size];
        }
    };
    private String placeAddress;
    private String snackTypes;
    private String placeID;
    private String latlong;

    public SearchBundleData() {

    }

    // Parcelling part
    public SearchBundleData(Parcel in) {
        String[] data = new String[4];
        in.readStringArray(data);
        this.placeAddress = data[0];
        this.snackTypes = data[1];
        this.placeID = data[2];
        this.latlong = data[3];
    }

    public String getplaceAddress() {
        return placeAddress;
    }

    public void setplaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getsnackTypes() {
        return snackTypes;
    }

    public void setsnackTypes(String snackTypes) {
        this.snackTypes = snackTypes;
    }

    public String getplaceId() {
        return placeID;
    }

    public void setplaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getlatlong() {
        return latlong;
    }

    public void setlatlong(String latlong) {
        this.latlong = latlong;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.placeAddress,
                this.snackTypes,
                this.placeID,
                this.latlong});
    }
}

