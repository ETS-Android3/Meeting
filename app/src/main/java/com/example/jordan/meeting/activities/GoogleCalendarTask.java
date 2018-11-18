package com.example.jordan.meeting.activities;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.jordan.meeting.R;
import com.example.jordan.meeting.database.Attendee;
import com.example.jordan.meeting.database.Meeting;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

public class GoogleCalendarTask extends AsyncTask<Void, Void, String> {

    private GoogleAccountCredential credential;
    private final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    private final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    private String tag = "events";
    private Meeting meeting;
    private MeetingView meetingView;
    boolean retry = false;

    GoogleCalendarTask(final Meeting meeting, final MeetingView meetingView){
        this.meeting = meeting;
        this.meetingView = meetingView;
    }

    void getAccountName(Intent data) {

        /* Get account name */
        String accountName =
                Objects.requireNonNull(data.getExtras()).getString(AccountManager.KEY_ACCOUNT_NAME);
        if (accountName != null) {
            credential.setSelectedAccountName(accountName);
            SharedPreferences settings = meetingView.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("accountName", accountName);
            editor.apply();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        String token;

        /* Getting Google account credential */
        credential =
                GoogleAccountCredential.usingOAuth2(meetingView, Collections.singleton(CalendarScopes.CALENDAR));
        SharedPreferences settings = meetingView.getPreferences(Context.MODE_PRIVATE);
        credential.setSelectedAccountName(settings.getString("accountName", null));

        /* Getting Google calendar client */
        com.google.api.services.calendar.Calendar client = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential).setApplicationName(meetingView.getString(R.string.app_name))
                .build();

        /* Asking user for choosing Google account */
        if (credential.getSelectedAccountName() == null) {
            meetingView.startActivityForResult(credential.newChooseAccountIntent(), MeetingView.ACCOUNT_REQUEST_CODE);
            retry = true;
            return null;
        }

        /* Asking user for permission if needed */
        try {
            token = GoogleAuthUtil.getToken(meetingView.getApplicationContext(), credential.getSelectedAccount(), credential.getScope());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (UserRecoverableAuthException e) {
            Log.d(tag, "Need permission");
            meetingView.startActivityForResult(e.getIntent(),MeetingView.PERMISSION_REQUEST_CODE);
            retry = true;
            return null;
        } catch (GoogleAuthException e) {
            e.printStackTrace();
            return null;
        }

        Event event = new Event()
                .setSummary(meeting.name)
                .setLocation(meeting.location)
                .setDescription(meetingView.getString(R.string.google_calendar_description));

        /* Parsing the meeting date */
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy-HH:mm", Locale.ENGLISH);
        DateTime startDateTime;
        try {
            startDateTime = new DateTime(dateFormatter.parse(meeting.date + "-" + meeting.time).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime);
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(startDateTime);
        event.setEnd(end);

        /* Setting attendees list */
        ArrayList<Integer> ids = meetingView.attendToRepo.getAttendeeIDs(meeting.meeting_ID);
        ArrayList<EventAttendee> attendees = new ArrayList<>();
        for (int id : ids){
            Attendee attendee = meetingView.attendeeRepo.getAttendeeById(id);
            EventAttendee eventAttendee = new EventAttendee();
            eventAttendee.setDisplayName(attendee.name);
            eventAttendee.setEmail(attendee.name + "@email.com");
            attendees.add(eventAttendee);
            Log.d(tag, "GoogleCalendarTask " + attendee.name + " added to attendees");
        }
        event.setAttendees(attendees);

        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        try {
            event = client.events().insert(calendarId, event).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(tag, "Event created: " + event.getHtmlLink());

        return  token;
    }

    @Override
    protected void onPostExecute(String token) {
        Log.d(tag, "Access token retrieved: " + token);
        if (token == null && !retry)
            Toast.makeText(meetingView, R.string.toast_google_calendar_sync_fail, Toast.LENGTH_SHORT).show();
        else if(token != null)
            Toast.makeText(meetingView, R.string.toast_google_calendar_sync_success, Toast.LENGTH_SHORT).show();
    }
}
