package com.myctca.model;

import android.util.Log;
import android.widget.Toast;

import com.myctca.util.MyCTCADateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import static com.myctca.MyCTCA.getAppContext;

/**
 * Created by tomackb on 2/13/18.
 */

public class ClinicalSummary implements Serializable {

    private String uniqueId;
    private String title;
    private String creationTime;
    private String authors;
    private String facilityName;
    private String docType;
    private String community;

    public String getCreationTime() {
        return creationTime;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getTitle() {
        return title;
    }

    public String getMonthDayCreatedString() {
        return MyCTCADateUtils.getMonthDateStr(MyCTCADateUtils.convertStringToLocalDate(creationTime));
    }

    public String getSlashFormatCreatedString() {
        return MyCTCADateUtils.getSlashedDateStr(MyCTCADateUtils.convertStringToLocalDate(creationTime));
    }

    public String getAuthors() {
        return authors;
    }

    public String getFacilityName() {
        return facilityName;
    }
}
