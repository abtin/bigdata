package com.novadox.bigdata.common.model;

import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@ToString
public class Person implements Serializable {
    private String firstName;
    private String lastName;
    private String address;
    private Date dateOfBirth;

    public Person() {
    }
//
//    @Override
//    public String toString() {
//        return "Person{" +
//                "firstName='" + firstName + '\'' +
//                ", lastName='" + lastName + '\'' +
//                ", address='" + address + '\'' +
//                ", dateOfBirth=" + dateOfBirth +
//                '}';
//    }

    public String getKey() {
        return lastName + "|" + firstName + "|" + dateOfBirth;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}