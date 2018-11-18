package com.example.jordan.meeting.activities;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.jordan.meeting.R;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class GoogleCalendarTask extends AsyncTask<Void, Void, String> {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private GoogleAccountCredential credential;
    private final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    private final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    private String tag = "events";
    private Meeting meeting;
    private MeetingView meetingView;

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
        String token = null;

        /* Google Accounts */
        credential =
                GoogleAccountCredential.usingOAuth2(meetingView, Collections.singleton(CalendarScopes.CALENDAR));
        SharedPreferences settings = meetingView.getPreferences(Context.MODE_PRIVATE);
        credential.setSelectedAccountName(settings.getString("accountName", null));

        /* Calendar client */
        com.google.api.services.calendar.Calendar client = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential).setApplicationName(meetingView.getString(R.string.app_name))
                .build();

        /* Asking user for choosing Google account */
        if (credential.getSelectedAccountName() == null) {
            meetingView.startActivityForResult(credential.newChooseAccountIntent(), MeetingView.ACCOUNT_REQUEST_CODE);
            return null;
        }

        /* Asking user for permission if needed */
        try {
            token = GoogleAuthUtil.getToken(meetingView.getApplicationContext(), credential.getSelectedAccount(), credential.getScope());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UserRecoverableAuthException e) {
            Log.d(tag, "Need permission");
            meetingView.startActivityForResult(e.getIntent(),PERMISSION_REQUEST_CODE);
        } catch (GoogleAuthException e) {
            e.printStackTrace();
        }

        Event event = new Event()
                .setSummary(meeting.name)
                .setLocation(meeting.location)
                .setDescription(meetingView.getString(R.string.google_calendar_description));

        DateTime startDateTime = new DateTime("2018-05-28T09:00:00-07:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setStart(start);

        DateTime endDateTime = new DateTime("2018-05-28T17:00:00-07:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setEnd(end);

        EventAttendee[] attendees = new EventAttendee[] {
                new EventAttendee().setEmail("lpage@example.com"),
                new EventAttendee().setEmail("sbrin@example.com"),
        };
        event.setAttendees(Arrays.asList(attendees));

        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("email").setMinutes(24 * 60),
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
        System.out.printf("Event created: %s\n", event.getHtmlLink());

        return  token;
    }

    @Override
    protected void onPostExecute(String token) {
        Log.d(tag, "Access token retrieved:" + token);
    }
}
