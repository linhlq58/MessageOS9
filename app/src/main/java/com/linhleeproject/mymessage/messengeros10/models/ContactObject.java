package com.linhleeproject.mymessage.messengeros10.models;

/**
 * Created by Linh Lee on 12/2/2016.
 */
public class ContactObject {
    private String name;
    private String phoneNumber;

    public ContactObject(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public ContactObject() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String toString() {
        return name + " " + phoneNumber;
    }
}
