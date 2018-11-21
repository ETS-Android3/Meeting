package com.example.jordan.meeting.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.jordan.meeting.R;
import com.example.jordan.meeting.database.Meeting;
import com.example.jordan.meeting.repositories.MeetingRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import static com.example.jordan.meeting.R.layout.meeting_entry;

public class MeetingFragment extends Fragment {

    private static final int MEETING_VIEW_REQUEST_CODE = 1;

    TextView meeting_Id;

    MeetingRepo repo;

    ListView meetingListView;

    String tag = "events";

    SharedPreferences sharedPref;

    private float fontSize;
    private int fontColor;
    private boolean isPast;

    public MeetingFragment() {super();}

    @SuppressLint("ValidFragment")
    public MeetingFragment(boolean isPast){
        super();
        this.isPast = isPast;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_meeting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        /* Setting ListView onItemClick callback function */
        meetingListView = view.findViewById(R.id.list);
        repo = new MeetingRepo(getContext());
        meetingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                meeting_Id = view.findViewById(R.id.meeting_Id);
                String meetingId = meeting_Id.getText().toString();
                Intent indent = new Intent(Objects.requireNonNull(getContext()).getApplicationContext(),
                        MeetingView.class);
                indent.putExtra("meeting_Id", Integer.parseInt(meetingId));
                startActivityForResult(indent, MEETING_VIEW_REQUEST_CODE);

                /* Setting activity transition */
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.translate_in_from_right,
                        R.anim.translate_out_to_left);
            }
        });

        /* Getting preferences */
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        /* Setting ListView adapter */
        updateMeetingListView();
    }

    public void updateMeetingListView() {

        /* Getting preferences */
        String prefFontSize = sharedPref.getString("fontSize", "-1");
        final String prefFontColor = sharedPref.getString("fontColor", "-1");
        Log.d(tag, "updateMeetingListView size: " + prefFontSize + " color " + prefFontColor);
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
            if (meeting.isPast() && !this.isPast)
                continue;
            if (!meeting.isPast() && this.isPast)
                continue;
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
        ListAdapter adapter = new SimpleAdapter(getContext(), meetingList, meeting_entry,
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
