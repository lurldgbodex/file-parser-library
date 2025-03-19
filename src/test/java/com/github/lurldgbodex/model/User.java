package com.github.lurldgbodex.model;

import com.github.lurldgbodex.annotations.FieldMapping;
import com.github.lurldgbodex.annotations.FileFormat;
import com.github.lurldgbodex.annotations.ValidationRule;
import com.github.lurldgbodex.enums.Format;

@FileFormat(format = Format.CSV)
public class User {
    @FieldMapping(column = "id")
    @ValidationRule(required = true, maxLength = 20)
    private int userId;
    @FieldMapping(column = "name")
    @ValidationRule(required = true, minLength = 2)
    private String name;
    @FieldMapping(column = "email")
    @ValidationRule(regex = "^[A-Za-z0-9+_.-]+@(.+)$")
    private String email;

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
