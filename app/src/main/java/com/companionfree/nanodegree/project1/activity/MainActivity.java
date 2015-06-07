package com.companionfree.nanodegree.project1.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Toast;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.fragment.ArtistSearchFragment;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new ArtistSearchFragment())
                    .commit();
        }

    }
    @Override
    public void onBackPressed() {
//        EventBus.getDefault().post(new ClickEvent());
        // do something on back.
        Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
        return;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        EventBus.getDefault().post(new ClickEvent());
        Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
