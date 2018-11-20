package com.example.jordan.meeting.database;

import android.support.annotation.NonNull;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Meeting implements Comparable<Meeting> {
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

    /* Constructors */
    public Meeting(){}

    public Meeting(int id, String name, String date, String time) {
        this.meeting_ID = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    /* Meeting implements Comparable<Meeting>
     * in order to be able to sort the meeting list by date.
     * */
    @Override
    public int compareTo(@NonNull Meeting meeting) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy-HH:mm", Locale.ENGLISH);
        Date thisDate;
        Date date;
        try {
            thisDate = new Date(dateFormatter.parse(this.date + "-" + this.time).getTime());
            date = new Date(dateFormatter.parse(meeting.date + "-" + meeting.time).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("Meeting", "Parse error");
            return 0;
        }
        Log.d("Meeting", this + " compared to " + meeting + " = " + thisDate.compareTo(date));
        return thisDate.compareTo(date);
    }

    @Override
    public String toString(){
        return this.name;
    }
}
