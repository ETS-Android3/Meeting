package com.example.jordan.meeting.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.jordan.meeting.R;
import com.example.jordan.meeting.database.Meeting;
import com.example.jordan.meeting.repositories.MeetingRepo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Meeting meeting;
    private String tag = "events";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(tag, "MapActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /* Create repositories */
        MeetingRepo meetingRepo = new MeetingRepo(this);

        /* Getting meeting */
        Intent intent = getIntent();
        int _Meeting_Id = intent.getIntExtra("meeting_Id", 0);
        meeting = meetingRepo.getMeetingById(_Meeting_Id);

        /* Setting actionBar */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            /* Show the Up button in the action bar */
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(meeting.name);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(tag, "MapActivity onMapReady");
        List<Address> AddressList = null;

        /* Getting Address from location String */
        Log.d(tag, "Getting address from location: " + meeting.location);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
           AddressList = geocoder.getFromLocationName(meeting.location, 1);
        } catch (IOException e) {
            Log.d(tag, "Geocode: " + e);
        }
        if ((AddressList != null) && (!AddressList.isEmpty())) {
            Log.d(tag, "Address found: " + AddressList.get(0).toString());
            Address meetingAddress = AddressList.get(0);
            LatLng meetingPoint = new LatLng(meetingAddress.getLatitude(), meetingAddress.getLongitude());

            /* Add a marker on the meeting place, move the camera and zoom */
            googleMap.addMarker(new MarkerOptions().position(meetingPoint).title(meetingAddress.getAddressLine(0)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(meetingPoint, 14.0f));
        } else {
            Log.d(tag, "Address not found");
            Toast.makeText(this, R.string.feedback_address_not_found, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void finish(){
        Log.d(tag, "MapActivity finish");
        Intent data = new Intent();
        data.putExtra("meeting_Id", meeting.meeting_ID);
        setResult(RESULT_OK, data);
        super.finish();
    }
}
