package com.example.jordan.meeting.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.jordan.meeting.database.AttendTo;
import com.example.jordan.meeting.database.DBHelper;

import java.util.ArrayList;

public class AttendToRepo {
    private DBHelper dbHelper;
    private String tag = "events";

    public AttendToRepo(Context context) {
        Log.d(tag, "AttendToRepo constructor");
        dbHelper = new DBHelper(context);
    }

    public int insert(AttendTo attendTo) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AttendTo.KEY_attendee, attendTo.attendee_ID);
        values.put(AttendTo.KEY_meeting, attendTo.meeting_ID);

        // Inserting Row
        long attendTo_Id = db.insert(AttendTo.TABLE, null, values);
        db.close(); // Closing database connection
        return (int) attendTo_Id;
    }

    public void delete(int attendee_ID, int meeting_ID) {
        Log.d(tag, "AttendTo delete attendee: " + attendee_ID + " meeting: " + meeting_ID);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        Log.d(tag, "AttendTo delete: " + db.delete(AttendTo.TABLE, AttendTo.KEY_attendee + "= ?" + " AND " + AttendTo.KEY_meeting + "= ?",
                new String[]{String.valueOf(attendee_ID), String.valueOf(meeting_ID)}));
        db.close(); // Closing database connection
    }

    public void update(AttendTo attendTo) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(AttendTo.KEY_meeting, attendTo.meeting_ID);
        values.put(AttendTo.KEY_attendee, attendTo.attendee_ID);

        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(AttendTo.TABLE, values,
                AttendTo.KEY_attendee + "= ?" + " AND " + AttendTo.KEY_meeting + "= ?",
                new String[]{String.valueOf(attendTo.attendee_ID), String.valueOf(attendTo.meeting_ID)});
        db.close(); // Closing database connection
    }

    public ArrayList<Integer> getAttendeeIDs(int meetingId) {
        Log.d(tag, "AttendToRepo getAttendeeIDs " + meetingId);
        ArrayList<Integer> attendeeIDs = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                AttendTo.KEY_attendee +
                " FROM " + AttendTo.TABLE
                + " WHERE " +
                AttendTo.KEY_meeting + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(meetingId)});
        if (cursor.moveToFirst()) {
            do {
                attendeeIDs.add(cursor.getInt(cursor.getColumnIndex(AttendTo.KEY_attendee)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return attendeeIDs;
    }

    public ArrayList<Integer> getMeetingIDs(int attendee_ID) {
        Log.d(tag, "AttendToRepo getMeetingsIDs " + attendee_ID);
        ArrayList<Integer> meetingIDs = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                AttendTo.KEY_meeting +
                " FROM " + AttendTo.TABLE
                + " WHERE " +
                AttendTo.KEY_attendee + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(attendee_ID)});
        if (cursor.moveToFirst()) {
            do {
                meetingIDs.add(cursor.getInt(cursor.getColumnIndex(AttendTo.KEY_meeting)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return meetingIDs;
    }
}


