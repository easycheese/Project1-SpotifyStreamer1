package com.companionfree.nanodegree.project1.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.companionfree.nanodegree.project1.R;

/**
 * Created by Laptop on 7/26/2015
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

    }

}
