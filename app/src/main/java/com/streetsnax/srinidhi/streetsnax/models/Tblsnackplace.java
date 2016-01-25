package com.streetsnax.srinidhi.streetsnax.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by I16881 on 20-Jan-16.
 */
// make it parcelable for passing from contactListActivity to contactViewActivity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Tblsnackplace extends BaseRecord {

    @JsonProperty("SnackPlaceID")
    public Integer SnackPlaceID;
    @JsonProperty("SnackPlaceName")
    public String SnackPlaceName;
    @JsonProperty("SnackType")
    public String SnackType;
    @JsonProperty("Locality")
    public String Locality;
    @JsonProperty("LandMark")
    public String LandMark;
    @JsonProperty("Description")
    public String Description;
    @JsonProperty("DaysAvailable")
    public Integer DaysAvailable;
    @JsonProperty("StartTimings")
    public String StartTimings;
    @JsonProperty("EndTimings")
    public String EndTimings;
    @JsonProperty("GoogleMapsName")
    public String GoogleMapsName;
    @JsonProperty("GoogleMapsAddress")
    public String GoogleMapsAddress;
    @JsonProperty("GoogleMapsLatitude")
    public double GoogleMapsLatitude;
    @JsonProperty("GoogleMapsLongitude")
    public double GoogleMapsLongitude;
    @JsonProperty("ImagePath")
    public String ImagePath;
    @JsonProperty("PlaceID")
    public String PlaceID;

    @Override
    public void setAllNonNull() {
        //TODO similar to the below for all the properties

    }
}