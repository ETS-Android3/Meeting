package com.example.jordan.meeting.database;

import android.location.Location;

public class Meeting {
    // Database table name
    public static final String TABLE = "Meeting";

    // Database keys
    public static final String KEY_ID = "id";
    public static final String KEY_name = "name";
    public static final String KEY_date = "date";
    public static final String KEY_time = "time";
    public static final String KEY_notes = "notes";
    public static final String KEY_location = "location";

    // Meeting attributes
    public int meeting_ID;
    public String name;
    public String date;
    public String time;
    public String notes;
    public String location;
}
