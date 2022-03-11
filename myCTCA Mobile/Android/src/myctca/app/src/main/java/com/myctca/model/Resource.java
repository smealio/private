package com.myctca.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Resource implements Parcelable {
    public static final Creator<Resource> CREATOR = new Creator<Resource>() {
        @Override
        public Resource createFromParcel(Parcel in) {
            return new Resource(in);
        }

        @Override
        public Resource[] newArray(int size) {
            return new Resource[size];
        }
    };
    private String title;
    private String resourceName;

    protected Resource(Parcel in) {
        title = in.readString();
        resourceName = in.readString();
    }

    public String getResourceName() {
        return resourceName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(resourceName);
    }
}
