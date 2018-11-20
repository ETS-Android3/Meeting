package com.example.jordan.meeting.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.jordan.meeting.database.DBHelper;
import com.example.jordan.meeting.database.Meeting;

import java.util.ArrayList;
import java.util.HashMap;

public class MeetingRepo {
    private DBHelper dbHelper;
    private String tag = "events";

    public MeetingRepo(Context context) {
        Log.d(tag, "MeetingRepo constructor");
        dbHelper = new DBHelper(context);
    }

    public int insert(Meeting meeting) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Meeting.KEY_name, meeting.name);
        values.put(Meeting.KEY_date, meeting.date);
        values.put(Meeting.KEY_time, meeting.time);
        values.put(Meeting.KEY_location, meeting.location);
        values.put(Meeting.KEY_notes, meeting.notes);

        // Inserting Row
        long meeting_Id = db.insert(Meeting.TABLE, null, values);
        db.close(); // Closing database connection
        return (int) meeting_Id;
    }

    public void delete(int meeting_Id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        db.delete(Meeting.TABLE, Meeting.KEY_ID + "= ?", new String[]{String.valueOf(meeting_Id)});
        db.close(); // Closing database connection
    }

    public void update(Meeting meeting) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Meeting.KEY_name, meeting.name);
        values.put(Meeting.KEY_date, meeting.date);
        values.put(Meeting.KEY_time, meeting.time);
        values.put(Meeting.KEY_location, meeting.location);
        values.put(Meeting.KEY_notes, meeting.notes);

        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(Meeting.TABLE, values, Meeting.KEY_ID + "= ?", new String[]{String.valueOf(meeting.meeting_ID)});
        db.close(); // Closing database connection
    }

    public ArrayList<HashMap<String, String>> getMeetingList() {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                Meeting.KEY_ID + "," +
                Meeting.KEY_name + "," +
                Meeting.KEY_date + "," +
                Meeting.KEY_notes + "," +
                Meeting.KEY_location + "," +
                Meeting.KEY_time +
                " FROM " + Meeting.TABLE;

        ArrayList<HashMap<String, String>> meetingList = new ArrayList<>();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> meeting = new HashMap<>();
                meeting.put("id", cursor.getString(cursor.getColumnIndex(Meeting.KEY_ID)));
                meeting.put("name", cursor.getString(cursor.getColumnIndex(Meeting.KEY_name)));
                meeting.put("date", cursor.getString(cursor.getColumnIndex(Meeting.KEY_date)));
                meeting.put("time", cursor.getString(cursor.getColumnIndex(Meeting.KEY_time)));
                meeting.put("location", cursor.getString(cursor.getColumnIndex(Meeting.KEY_location)));
                meeting.put("notes", cursor.getString(cursor.getColumnIndex(Meeting.KEY_notes)));
                meetingList.add(meeting);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return meetingList;

    }

    public Meeting getMeetingById(int Id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                Meeting.KEY_ID + "," +
                Meeting.KEY_name + "," +
                Meeting.KEY_date + "," +
                Meeting.KEY_notes + "," +
                Meeting.KEY_location + "," +
                Meeting.KEY_time +
                " FROM " + Meeting.TABLE
                + " WHERE " +
                Meeting.KEY_ID + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        Meeting meeting = new Meeting();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(Id)});

        if (cursor.moveToFirst()) {
            do {
                meeting.meeting_ID = cursor.getInt(cursor.getColumnIndex(Meeting.KEY_ID));
                meeting.name = cursor.getString(cursor.getColumnIndex(Meeting.KEY_name));
                meeting.date = cursor.getString(cursor.getColumnIndex(Meeting.KEY_date));
                meeting.time = cursor.getString(cursor.getColumnIndex(Meeting.KEY_time));
                meeting.location = cursor.getString(cursor.getColumnIndex(Meeting.KEY_location));
                meeting.notes = cursor.getString(cursor.getColumnIndex(Meeting.KEY_notes));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return meeting;
    }
}


