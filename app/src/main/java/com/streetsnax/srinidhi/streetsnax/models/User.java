package com.streetsnax.srinidhi.streetsnax.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by I15230 on 12/26/2015.
 */
// make it parcelable for passing from contactListActivity to contactViewActivity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User extends BaseRecord {

    @JsonProperty("UserID")
    public Integer UserID;
    @JsonProperty("UserName")
    public String UserName;
    @JsonProperty("Email")
    public String Email;
    @JsonProperty("Password")
    public String Password;
    @JsonProperty("AddressLine1")
    public String AddressLine1;
    @JsonProperty("AddressLine2")
    public String AddressLine2;
    @JsonProperty("Area")
    public String Area;
    @JsonProperty("City")
    public String City;
    @JsonProperty("State")
    public String State;
    @JsonProperty("Country")
    public String Country;
    @JsonProperty("Phone")
    public String Phone;
    @JsonProperty("Active")
    public Boolean Active;
    @JsonProperty("Gender")
    public String Gender;
    @JsonProperty("TypeOfLogin")
    public Integer TypeOfLogin;

    @Override
    public void setAllNonNull() {
        //TODO similar to the below for all the properties
        UserName = getNonNull(UserName);
    }
}