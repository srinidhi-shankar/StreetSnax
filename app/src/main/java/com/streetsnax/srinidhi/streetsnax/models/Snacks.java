package com.streetsnax.srinidhi.streetsnax.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by I16881 on 27-Dec-15.
 */
public class Snacks extends BaseRecord {

    @JsonProperty("resource")
    public List<Snack> snackRecord = new ArrayList<>();
}

