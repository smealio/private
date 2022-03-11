package com.myctca.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.myctca.util.MyCTCADateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by tomackb on 12/18/17.
 */
@SuppressWarnings("serial")
public class Appointment implements Parcelable {

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };
    private static final String TAG = "APPOINTMENT";
    private String description;
    private String location;
    private String startDateTime = "";
    private String endDateTime;
    private String resources;
    private List<Resource> resourceList;
    private String additionalInfo;
    private String status;
    private String appointmentId;
    private String patientInstructions;
    private String schedulerNotes;
    private boolean isTeleHealth;
    private String teleHealthUrl;
    private String telehealthInfoUrl;
    private String facilityName;
    private String facilityAddress1;
    private String facilityAddress2;
    private String facilityCity;
    private String facilityState;
    private String facilityPostalCode;
    private String facilityMainPhone;
    private String facilitySchedulingPhone;
    private String facilityAccommodationsPhone;
    private String facilityTransportationPhone;
    private String facilityTimeZone;
    private String telehealthMeetingJoinUrl;
    private String meetingId;

    protected Appointment(Parcel in) {
        description = in.readString();
        location = in.readString();
        startDateTime = in.readString();
        endDateTime = in.readString();
        resources = in.readString();
        resourceList = new ArrayList<>();
        in.readList(resourceList, Resource.class.getClassLoader());
        additionalInfo = in.readString();
        status = in.readString();
        appointmentId = in.readString();
        patientInstructions = in.readString();
        schedulerNotes = in.readString();
        isTeleHealth = in.readByte() != 0;
        teleHealthUrl = in.readString();
        telehealthInfoUrl = in.readString();
        facilityName = in.readString();
        facilityAddress1 = in.readString();
        facilityAddress2 = in.readString();
        facilityCity = in.readString();
        facilityState = in.readString();
        facilityPostalCode = in.readString();
        facilityMainPhone = in.readString();
        facilitySchedulingPhone = in.readString();
        facilityAccommodationsPhone = in.readString();
        facilityTransportationPhone = in.readString();
        facilityTimeZone = in.readString();
        telehealthMeetingJoinUrl = in.readString();
        meetingId = in.readString();
    }

    public String getMeetingId() {
        return meetingId;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public String getTelehealthMeetingJoinUrl() {
        return telehealthMeetingJoinUrl;
    }

    public String getTeleHealthUrl() {
        return teleHealthUrl;
    }

    public String getTelehealthInfoUrl() {
        return telehealthInfoUrl;
    }

    public boolean getUpcoming() {
        boolean isUpcoming = false;

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(getStartDateLocal());
        cal1.add(Calendar.MINUTE, 30);

        if (cal1.getTime().after(new Date())) {
            isUpcoming = true;
        }
        return isUpcoming;
    }

    public String getStartDateString() {

        String startDateString;

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
        startDateString = sdf.format(getStartDate());

        return startDateString;
    }

    public String getStartTimeString() {

        String startTimeString = "";

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
        startTimeString = sdf.format(getStartDate());

        return startTimeString;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public String getResourceList() {
        String resources = "";
        if (resourceList != null && !resourceList.isEmpty()) {
            for (int i = 0; i < resourceList.size() - 1; i++) {
                if (resourceList.get(i).getResourceName() != null)
                    resources = resources.concat(resourceList.get(i).getResourceName()).concat("\n");
            }
            if (resourceList.get(resourceList.size() - 1).getResourceName() != null)
                resources = resources.concat(resourceList.get(resourceList.size() - 1).getResourceName());
        }
        return resources;
    }

    public String getPatientInstructions() {
        return patientInstructions;
    }

    public String getSchedulerNotes() {
        return schedulerNotes;
    }


    public String getStartDateTime() {
        return startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean getTelehealth() {
        return isTeleHealth;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getFacilityAddress1() {
        return facilityAddress1;
    }

    public String getFacilityAddress2() {
        return facilityAddress2;
    }

    public String getFacilityCity() {
        return facilityCity;
    }

    public String getFacilityState() {
        return facilityState;
    }

    public String getFacilityPostalCode() {
        return facilityPostalCode;
    }

    public String getFacilityMainPhone() {
        return facilityMainPhone;
    }

    public String getFacilitySchedulingPhone() {
        return facilitySchedulingPhone;
    }

    public String getFacilityAccommodationsPhone() {
        return facilityAccommodationsPhone;
    }

    public String getFacilityTransportationPhone() {
        return facilityTransportationPhone;
    }

    public String getFacilityTimeZone() {
        return facilityTimeZone;
    }

    public Date getStartDateLocal() {
        return MyCTCADateUtils.convertAppointmentStringToLocalDate(startDateTime, facilityTimeZone);
    }

    public Date getStartDate() {
        return MyCTCADateUtils.convertStringToLocalDate(startDateTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(description);
        parcel.writeString(location);
        parcel.writeString(startDateTime);
        parcel.writeString(endDateTime);
        parcel.writeString(resources);
        parcel.writeList(resourceList);
        parcel.writeString(additionalInfo);
        parcel.writeString(status);
        parcel.writeString(appointmentId);
        parcel.writeString(patientInstructions);
        parcel.writeString(schedulerNotes);
        parcel.writeByte((byte) (isTeleHealth ? 1 : 0));
        parcel.writeString(teleHealthUrl);
        parcel.writeString(telehealthInfoUrl);
        parcel.writeString(facilityName);
        parcel.writeString(facilityAddress1);
        parcel.writeString(facilityAddress2);
        parcel.writeString(facilityCity);
        parcel.writeString(facilityState);
        parcel.writeString(facilityPostalCode);
        parcel.writeString(facilityMainPhone);
        parcel.writeString(facilitySchedulingPhone);
        parcel.writeString(facilityAccommodationsPhone);
        parcel.writeString(facilityTransportationPhone);
        parcel.writeString(facilityTimeZone);
        parcel.writeString(telehealthMeetingJoinUrl);
        parcel.writeString(meetingId);
    }
}
