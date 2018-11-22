package com.example.jordan.meeting.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.jordan.meeting.fragments.MeetingFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int index) {

        String tag = "events";
        switch (index) {

            case 0:
                Log.d(tag, "getItem Current list");

                /* List of current meeting */
                Bundle bundleCurrentList = new Bundle();
                bundleCurrentList.putBoolean("isPast", false);
                MeetingFragment currentList = new MeetingFragment();
                currentList.setArguments(bundleCurrentList);
                return currentList;

            case 1:
                Log.d(tag, "getItem past list");

                /* List of past meetings */
                Bundle bundlePastList = new Bundle();
                bundlePastList.putBoolean("isPast", true);
                MeetingFragment pastList = new MeetingFragment();
                pastList.setArguments(bundlePastList);
                return pastList;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}