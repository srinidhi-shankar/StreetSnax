package com.streetsnax.srinidhi.streetsnax.models;

/**
 * Created by I15230 on 12/26/2015.
 */
// no real functionality, just something to extend for polymorphism
public class BaseRecord {
    public String getNonNull(String toCheck) {
        // just so we don't go display a null string
        if (toCheck == null) {
            return "";
        }
        return toCheck;
    }

    public void setAllNonNull() {
    }
}
