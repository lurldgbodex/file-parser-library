package com.github.lurldgbodex.model;

import com.github.lurldgbodex.annotations.FieldMapping;
import com.github.lurldgbodex.annotations.ValidationRule;

public class Person {
    private int id;

    @FieldMapping(column = "name")
    @ValidationRule(required = true, minLength = 3)
    private String fullName;
    private int age;

    @ValidationRule(regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;
    @FieldMapping(column = "sex", type = Gender.class)
    private Gender gender;
    private boolean single;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }
}
