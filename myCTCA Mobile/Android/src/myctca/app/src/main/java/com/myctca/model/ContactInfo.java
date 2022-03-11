package com.myctca.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ContactInfo {
    private String contactId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String dateOfBirth;
    private String primaryFacility;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String addressChangeType;
    private String addressCreatedBy;
    private String emailAddress;
    private String emailAddressChangeType;
    private List<PhoneNumber> phoneNumbers;
    private String changeType;
    private String epiId;

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public Date getUserDob() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return sdf.parse(dateOfBirth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }
}