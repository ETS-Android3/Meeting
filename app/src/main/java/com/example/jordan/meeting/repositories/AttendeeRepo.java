package com.example.jordan.meeting.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.jordan.meeting.database.Attendee;
import com.example.jordan.meeting.database.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class AttendeeRepo {
    private DBHelper dbHelper;
    private String tag = "events";

    public AttendeeRepo(Context context) {
        Log.d(tag, "AttendeeRepo constructor");
        dbHelper = new DBHelper(context);
    }

    public int insert(Attendee attendee) {
        Log.d(tag, "AttendeeRepo insert");

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Attendee.KEY_name, attendee.name);
        Log.d(tag, attendee.name);
        // Inserting Row
        long attendee_Id = db.insert(Attendee.TABLE, null, values);
        db.close(); // Closing database connection
        return (int) attendee_Id;
    }

    public void delete(int attendee_Id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        db.delete(Attendee.TABLE, Attendee.KEY_ID + "= ?", new String[]{String.valueOf(attendee_Id)});
        db.close(); // Closing database connection
    }

    /*public void update(Attendee attendee) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Attendee.KEY_name, attendee.name);
        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(Attendee.TABLE, values, Attendee.KEY_ID + "= ?", new String[]{String.valueOf(attendee.attendee_ID)});
        db.close(); // Closing database connection
    }*/

    public ArrayList<HashMap<String, String>> getAttendeeList() {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                Attendee.KEY_ID + "," +
                Attendee.KEY_name +
                " FROM " + Attendee.TABLE;

        ArrayList<HashMap<String, String>> attendeeList = new ArrayList<>();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> attendee = new HashMap<>();
                attendee.put("id", cursor.getString(cursor.getColumnIndex(Attendee.KEY_ID)));
                attendee.put("name", cursor.getString(cursor.getColumnIndex(Attendee.KEY_name)));
                attendeeList.add(attendee);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return attendeeList;

    }

    public Attendee getAttendeeById(int Id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                Attendee.KEY_ID + "," +
                Attendee.KEY_name +
                " FROM " + Attendee.TABLE
                + " WHERE " +
                Attendee.KEY_ID + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        Attendee attendee = new Attendee();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(Id)});

        if (cursor.moveToFirst()) {
            do {
                attendee.attendee_ID = cursor.getInt(cursor.getColumnIndex(Attendee.KEY_ID));
                attendee.name = cursor.getString(cursor.getColumnIndex(Attendee.KEY_name));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return attendee;
    }

    public Attendee getAttendeeByName(String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                Attendee.KEY_ID + "," +
                Attendee.KEY_name +
                " FROM " + Attendee.TABLE
                + " WHERE " +
                Attendee.KEY_name + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        Attendee attendee = new Attendee();
        attendee.attendee_ID = -1;

        Cursor cursor = db.rawQuery(selectQuery, new String[]{name});

        if (cursor.moveToFirst()) {
            do {
                attendee.attendee_ID = cursor.getInt(cursor.getColumnIndex(Attendee.KEY_ID));
                attendee.name = cursor.getString(cursor.getColumnIndex(Attendee.KEY_name));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return attendee;
    }
}
