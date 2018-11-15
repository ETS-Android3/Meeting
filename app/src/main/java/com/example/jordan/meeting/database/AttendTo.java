package com.example.jordan.meeting.database;

/* Table of association between attendees and meetings (Multi-Multi relation)*/
public class AttendTo {
    // Database table name
    public static final String TABLE = "AttendTo";

    // Database keys
    public static final String KEY_attendee = "attendee_ID";
    public static final String KEY_meeting = "meeting_ID";

    // AttendTo attributes
    public int attendee_ID;
    public int meeting_ID;
}
