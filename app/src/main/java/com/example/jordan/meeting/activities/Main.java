package com.example.jordan.meeting.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jordan.meeting.R;
import com.example.jordan.meeting.database.Meeting;
import com.example.jordan.meeting.repositories.MeetingRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import static com.example.jordan.meeting.R.layout.meeting_entry;

public class Main extends AppCompatActivity implements android.view.View.OnClickListener {

    private static final int MEETING_VIEW_REQUEST_CODE = 1;
    private static final int SETTINGS_REQUEST_CODE = 2;
    private static final int MEETING_EDIT_REQUEST_CODE = 3;

    TextView meeting_Id;
    FloatingActionButton btnNewMeeting;

    MeetingRepo repo;

    ListView meetingListView;

    String tag = "events";

    SharedPreferences sharedPref;

    private float fontSize;
    private int fontColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(tag, "Main onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Setting toolbar_meeting_view */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Floating new meeting button */
        btnNewMeeting = findViewById(R.id.btnNewMeeting);
        btnNewMeeting.setOnClickListener(this);

        /* Setting onItemClick callback function */
        meetingListView = findViewById(R.id.list);
        repo = new MeetingRepo(this);
        meetingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                meeting_Id = view.findViewById(R.id.meeting_Id);
                String meetingId = meeting_Id.getText().toString();
                Intent indent = new Intent(getApplicationContext(), MeetingView.class);
                indent.putExtra("meeting_Id", Integer.parseInt(meetingId));
                startActivityForResult(indent, MEETING_VIEW_REQUEST_CODE);
            }
        });

        /* Get preferences */
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        /* Setting ListView adapter */
        updateFontStyle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.d(tag, "Action settings");
                Intent intent = new Intent(this, Settings.class);
                startActivityForResult(intent, SETTINGS_REQUEST_CODE);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onClick(View view) {
        Log.d(tag, "Main onClick");
        if (view == findViewById(R.id.btnNewMeeting)) {
            Intent intent = new Intent(this, MeetingEdit.class);
            intent.putExtra("meeting", 0);
            startActivityForResult(intent, MEETING_EDIT_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(tag, "Main onActivityResult");
        if (resultCode == RESULT_OK) {

            if (data.hasExtra("returnKey"))
                Toast.makeText(this,
                        Objects.requireNonNull(data.getExtras()).getString("returnKey"),
                        Toast.LENGTH_SHORT).show();

            /* Refreshing meeting list */
            ArrayList<HashMap<String, String>> meetingList = repo.getMeetingList();
            ListAdapter adapter = new SimpleAdapter(Main.this, meetingList, meeting_entry,
                    new String[]{"id", "name", "date"},
                    new int[]{R.id.meeting_Id, R.id.meeting_name, R.id.meeting_date});
            meetingListView.setAdapter(adapter);

            /* Updating font style */
            updateFontStyle();
        }
    }

    private void updateFontStyle() {

        /* Getting preferences */
        String prefFontSize = sharedPref.getString("fontSize", "-1");
        final String prefFontColor = sharedPref.getString("fontColor", "-1");
        Log.d(tag, "updateFontStyle size: " + prefFontSize + " color " + prefFontColor);
        fontSize = Float.valueOf(prefFontSize);

        switch (Integer.valueOf(prefFontColor)){
            case 1:
                fontColor = getResources().getColor(R.color.colorBlack, null);
                break;

            case 2:
                fontColor = getResources().getColor(R.color.colorBlue, null);
                break;

            case 3:
                fontColor = getResources().getColor(R.color.colorGrey, null);
                break;

            default:
                fontColor = Color.BLACK;
        }

        /* Sorting meeting list by date */
        ArrayList<Meeting> meetingListSort = new ArrayList<>();
        for (HashMap<String, String> map : repo.getMeetingList()){
            Meeting meeting = new Meeting(Integer.valueOf(map.get("id")),
                    map.get("name"), map.get("date"), map.get("time"));
            meetingListSort.add(meeting);
        }
        Log.d(tag, "Meeting list: " + meetingListSort);
        Collections.sort(meetingListSort);
        Log.d(tag, "Sorted meeting list: " + meetingListSort);
        ArrayList<HashMap<String, String>> meetingList = new ArrayList<>();
        for (Meeting meeting : meetingListSort){
            HashMap<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(meeting.meeting_ID));
            map.put("name", String.valueOf(meeting.name));
            map.put("date", String.valueOf(meeting.date));
            map.put("time", String.valueOf(meeting.time));
            meetingList.add(map);
        }

        /* Hacking ListView adapter to update font style */
        ListAdapter adapter = new SimpleAdapter(Main.this, meetingList, meeting_entry,
                new String[]{"id", "name", "date"}, new int[]{R.id.meeting_Id, R.id.meeting_name, R.id.meeting_date}){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);

                TextView tv_name = view.findViewById(R.id.meeting_name);
                TextView tv_date = view.findViewById(R.id.meeting_date);

                /* Updating font size */
                tv_name.setTextSize(TypedValue.COMPLEX_UNIT_PT,fontSize);
                tv_date.setTextSize(TypedValue.COMPLEX_UNIT_PT,fontSize);

                /* Updating font color */
                tv_date.setTextColor(fontColor);
                tv_name.setTextColor(fontColor);

                return view;
            }
        };
        meetingListView.setAdapter(adapter);
    }
}
