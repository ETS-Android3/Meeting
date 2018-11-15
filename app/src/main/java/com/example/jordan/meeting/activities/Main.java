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
import com.example.jordan.meeting.repositories.MeetingRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.example.jordan.meeting.R.layout.meeting_entry;

public class Main extends AppCompatActivity implements android.view.View.OnClickListener {

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
                startActivityForResult(indent, 10);
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
                startActivityForResult(intent, 50);
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
            startActivityForResult(intent, 10);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(tag, "Main onActivityResult");
        if (resultCode == RESULT_OK)
            switch (requestCode){
                case 10:
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

                case 50:
                    Log.d(tag, "Outing of settings");
                    updateFontStyle();
        }
    }

    private void updateFontStyle() {

        /* Getting preferences */
        String prefFontSize = sharedPref.getString("fontSize", "-1");
        final String prefFontColor = sharedPref.getString("fontColor", "-1");
        Log.d(tag, "updateFontStyle size: " + prefFontSize + " color " + prefFontColor);

        if (!prefFontSize.equals("-1"))
            fontSize = Float.valueOf(prefFontSize);
        else
            Log.d(tag, "Error while reading font size preference");

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

        /* Hacking ListView adapter to update font style */
        ArrayList<HashMap<String, String>> meetingList = repo.getMeetingList();
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
