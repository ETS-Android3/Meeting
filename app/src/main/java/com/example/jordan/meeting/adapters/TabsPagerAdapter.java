package com.example.jordan.meeting.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.jordan.meeting.fragments.MeetingFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                /* List of current meeting */
                return new MeetingFragment(false);
            case 1:
                /* List of past meetings */
                return new MeetingFragment(true);
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}