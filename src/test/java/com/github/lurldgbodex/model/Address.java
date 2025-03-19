package com.github.lurldgbodex.model;

import com.github.lurldgbodex.annotations.FieldMapping;

public class Address {
    @FieldMapping(column = "city")
    private String city;

    @FieldMapping(column = "zip")
    private String zipCode;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Address() {}
}
