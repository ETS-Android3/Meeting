package com.example.jordan.meeting.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.jordan.meeting.R;
import com.example.jordan.meeting.adapters.TabsPagerAdapter;
import com.example.jordan.meeting.fragments.MeetingFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements android.view.View.OnClickListener {

    private static final int SETTINGS_REQUEST_CODE = 1;
    private static final int MEETING_NEW_REQUEST_CODE = 2;

    FloatingActionButton btnNewMeeting;

    String tag = "events";

    TabsPagerAdapter tabsPagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(tag, "MainActivity onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Setting toolbar_meeting_view */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Floating new meeting button */
        btnNewMeeting = findViewById(R.id.btnNewMeeting);
        btnNewMeeting.setOnClickListener(this);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_title_current_meeting));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_title_past_meeting));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = findViewById(R.id.pager);
        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Log.d(tag, tab.getPosition() + ": onTabSelected");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d(tag, tab.getPosition() + ": OnTabUnselected");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(tag, tab.getPosition() + ": onTabReselected");
            }
        });
    }

    @Override
    public void onResume(){
        Log.d(tag, "MainActivity onResume");
        super.onResume();

        /* Setting activity transition */
        overridePendingTransition(R.anim.translate_in_from_left, R.anim.translate_out_to_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /* Inflating menu */
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:

                /* Starting settings activity */
                Log.d(tag, "Action settings");
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_REQUEST_CODE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        Log.d(tag, "MainActivity onClick");
        if (view == findViewById(R.id.btnNewMeeting)) {

            /* Starting edit meeting activity */
            Intent intent = new Intent(this, MeetingEditActivity.class);
            intent.putExtra("meeting", 0);
            startActivityForResult(intent, MEETING_NEW_REQUEST_CODE);

            /* Setting activity transition */
            overridePendingTransition(R.anim.translate_in_from_right, R.anim.translate_out_to_left);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(tag, "MainActivity onActivityResult");

        if (resultCode != RESULT_OK){
            Log.d(tag, "onActivityResult: result code not OK");
            return;
        }
        Log.d(tag, "Request code = " + requestCode);

        switch(requestCode){

            case SETTINGS_REQUEST_CODE:
                Log.d(tag, "SETTINGS_REQUEST_CODE");

            case MeetingFragment.MEETING_VIEW_REQUEST_CODE:
                Log.d(tag, "MEETING_VIEW_REQUEST_CODE");

            case MEETING_NEW_REQUEST_CODE:
                Log.d(tag, "MEETING_NEW_REQUEST_CODE");

                /* User feedback */
                if (data.hasExtra("returnKey"))
                    Toast.makeText(this,
                            Objects.requireNonNull(data.getExtras()).getString("returnKey"),
                            Toast.LENGTH_SHORT).show();

                /* Updating meeting lists */
                tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
                viewPager.setAdapter(tabsPagerAdapter);
                break;

            default:
                Log.d(tag, "Request code unknown: " + requestCode);
        }
    }
}
