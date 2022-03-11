package com.myctca.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomackb on 2/23/18.
 */

public class MyCTCAUserProfile {

    private String epiId;
    private String ctcaId;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String primaryFacility;
    private String isSuperUser;
    private ArrayList<String> roles;
    private ArrayList<String> userPermissions = new ArrayList<>();
    private ArrayList<MyCTCAProxy> proxies = new ArrayList<>();

    public boolean userCan(String permission) {
        for (String uPermission : userPermissions) {
            if (uPermission.equals(permission)) {
                return true;
            }
        }
        return false;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {

        return this.firstName + " " + this.lastName;
    }

    public String getCtcaId() {
        return ctcaId;
    }

    public String getPrimaryFacility() {
        return primaryFacility;
    }

    public List<String> getViewablePatients() {

        List<String> viewablePatients = new ArrayList<>();

        viewablePatients.add(this.getFullName());

        for (MyCTCAProxy proxy : this.proxies) {
            viewablePatients.add(proxy.getFullName());
        }

        return viewablePatients;
    }

    public String getCTCAUniqueIdFromFullName(String fullName) {

        String[] nameAR = fullName.split(" ");
        String fName = nameAR[0];
        String lName = nameAR[1];

        for (MyCTCAProxy proxy : this.proxies) {
            if ((fName.equals(proxy.getFirstName())) && (lName.equals(proxy.getLastName()))) {
                return proxy.getToCtcaUniqueId();
            }
        }

        return "";
    }

    public String getEpiId() {
        return epiId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public ArrayList<MyCTCAProxy> getProxies() {
        return proxies;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }
}
