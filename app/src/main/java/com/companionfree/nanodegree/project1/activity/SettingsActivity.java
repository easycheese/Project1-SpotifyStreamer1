package com.companionfree.nanodegree.project1.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.fragment.SettingsFragment;

/**
 * Created by Laptop on 7/26/2015
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }


}
