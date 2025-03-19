package com.github.lurldgbodex.model;

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

    public Employee(){}

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
