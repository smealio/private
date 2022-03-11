package com.myctca.model;

import java.io.Serializable;

/**
 * Created by tomackb on 1/15/18.
 */

public class CareTeam implements Serializable {

    private String systemId;
    private String name;
    private String userName;
    private String environmentId;

    public String getName() {
        return name;
    }

    public String getSystemId() {
        return systemId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEnvironmentId() {
        return environmentId;
    }
}
