package com.streetsnax.srinidhi.streetsnax.utilities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by I15230 on 12/31/2015.
 */

public class ItemDetails implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ItemDetails createFromParcel(Parcel in) {
            return new ItemDetails(in);
        }

        public ItemDetails[] newArray(int size) {
            return new ItemDetails[size];
        }
    };
    private String placeAddress;
    private String snackType;
    private String itemImageSrc;
    private String placeID;
    private String latlong;
    private String snackPlaceName;

    public ItemDetails() {

    }

    // Parcelling part
    public ItemDetails(Parcel in) {
        String[] data = new String[6];
        in.readStringArray(data);
        this.placeAddress = data[0];
        this.snackType = data[1];
        this.itemImageSrc = data[2];
        this.placeID = data[3];
        this.snackPlaceName = data[4];
        this.latlong = data[5];
    }

    public String getplaceAddress() {
        return placeAddress;
    }

    public void setplaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getItemImageSrc() {
        return itemImageSrc;
    }

    public void setItemImageSrc(String itemImageSrc) {
        this.itemImageSrc = itemImageSrc;
    }

    public String getsnackType() {
        return snackType;
    }

    public void setsnackType(String snackType) {
        this.snackType = snackType;
    }

    public String getplaceId() {
        return placeID;
    }

    public void setplaceID(String placeID) {
        this.snackPlaceName = placeID;
    }

    public String getsnackPlaceName() {
        return snackPlaceName;
    }

    public void setsnackPlaceName(String snackPlaceName) {
        this.snackPlaceName = snackPlaceName;
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
                this.snackType,
                this.itemImageSrc,
                this.placeID,
                this.snackPlaceName,
                this.latlong});
    }
}

