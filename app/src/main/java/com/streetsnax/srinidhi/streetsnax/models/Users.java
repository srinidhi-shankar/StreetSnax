package com.streetsnax.srinidhi.streetsnax.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by I15230 on 12/26/2015.
 */
public class Users extends BaseRecord {
    @JsonProperty("resource")
    public List<User> userRecord = new ArrayList<>();
}
