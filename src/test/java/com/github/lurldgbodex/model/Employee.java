package com.github.lurldgbodex.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.lurldgbodex.annotations.FieldMapping;

import java.time.LocalDate;

public class Employee {
    @FieldMapping(column = "full_name")
    private String name;

    @FieldMapping(column = "years", type = Integer.class)
    private int age;

    @FieldMapping(column = "salary")
    private double salary;

    @FieldMapping(column = "active")
    private boolean employed;

    @FieldMapping(column = "start_date")
    private LocalDate startDate;

    @FieldMapping(column = "department")
    private Department department;

    @FieldMapping(column = "address")
    private Address address;

    private String id;
    private int code;
    private double su;
    private char stat;
    private boolean isFired;

    @JsonCreator
    public Employee(@JsonProperty("id") String id,
                    @JsonProperty("su") double su, @JsonProperty("code") int code,
                    @JsonProperty("stat") char stat, @JsonProperty("isFired") boolean isFired) {
        this.id = id;
        this.su = su;
        this.code = code;
        this.stat = stat;
        this.isFired = isFired;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public boolean isEmployed() {
        return employed;
    }

    public String getId() {
        return id;
    }

    public int getCode() {
        return code;
    }

    public double getSu() {
        return su;
    }

    public boolean getIsFired() {
        return isFired;
    }

    public char getStat() {
        return stat;
    }

    public void setEmployed(boolean employed) {
        this.employed = employed;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
