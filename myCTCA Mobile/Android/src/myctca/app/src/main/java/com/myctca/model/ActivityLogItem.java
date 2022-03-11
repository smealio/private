package com.myctca.model;

import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.myctca.util.MyCTCADateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityLogItem {

    private String userName;
    private String formattedMessage;
    private String details;
    private String formattedTimestamp;

    public String getDisplayTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm aa", Locale.US);
        return sdf.format(getDate());
    }

    public String getUsername() {
        if (TextUtils.isEmpty(userName)) {
            userName = "";
        }
        return userName;
    }

    public String getMessage() {
        return formattedMessage;
    }

    public Date getDate() {
        return MyCTCADateUtils.convertTimestampToLocalDate(formattedTimestamp, "yyyy-MM-dd hh:mm:ss.SSSZ");
    }
}
