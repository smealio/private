package com.myctca.model;

import org.json.JSONException;
import org.json.JSONObject;

public class FacilityInfoAll {
    public String name;
    public String displayName;
    public FacilityAddress address;
    public String mainPhone;
    public String schedulingPhone;
    public String accommodationsPhone;
    public String transportationPhone;
    public String himroiPhone;

    public FacilityInfoAll(JSONObject object){
        try {
            this.name = object.getString("name");
            this.displayName = object.getString("displayName");
            this.mainPhone = object.getString("mainPhone");
            this.schedulingPhone = object.getString("schedulingPhone");
            this.accommodationsPhone = object.getString("accommodationsPhone");
            this.transportationPhone = object.getString("transportationPhone");
            this.himroiPhone = object.getString("himroiPhone");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
