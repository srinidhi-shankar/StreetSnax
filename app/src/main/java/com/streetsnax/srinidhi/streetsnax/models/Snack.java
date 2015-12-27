package com.streetsnax.srinidhi.streetsnax.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by I16881 on 27-Dec-15.
 */

// make it parcelable for passing from contactListActivity to contactViewActivity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Snack extends BaseRecord {

    @JsonProperty("SnackID")
    public Integer SnackID;
    @JsonProperty("SnackType")
    public String SnackType;


    @Override
    public void setAllNonNull() {
        //TODO similar to the below for all the properties
        SnackType = getNonNull(SnackType);
    }
}