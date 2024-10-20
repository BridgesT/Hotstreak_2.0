package com.example.hotstreak;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotstreak.fragments.HistoryFragment;
import com.example.hotstreak.fragments.StreakFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.objectbox.Box;
import io.objectbox.BoxStore;

public class MainActivity extends AppCompatActivity {

    StreakFragment streakFragment;
    HistoryFragment historyFragment;

    Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        streakFragment = new StreakFragment();
        historyFragment = new HistoryFragment();

        // Add fragments to the FragmentManager
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, historyFragment, "historyFragment").hide(historyFragment)
                .add(R.id.container, streakFragment, "streakFragment")
                .commit();

        activeFragment = streakFragment;

        // Setup BottomNavigationView
        BottomNavigationView bottomNavView = findViewById(R.id.bottomNavigationView);
        bottomNavView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_streak:
                    selectedFragment = streakFragment;
                    break;
                case R.id.nav_history:
                    LayoutInflater inflater = getLayoutInflater();
                    ViewGroup container = findViewById(R.id.container);
                    historyFragment.loadStreakData();
                    historyFragment.updateTable(inflater, container);
                    selectedFragment = historyFragment;
                    break;
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(activeFragment).show(selectedFragment).commit();
                activeFragment = selectedFragment;
            }
            return true;
        });
    }


//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//
//        View rootView = findViewById(android.R.id.content).getRootView();
//
//        if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == 66){
//
//            //Toast.makeText(MainActivity.this, "pressed enter", Toast.LENGTH_SHORT).show();
//            updateAttemptStreak(rootView);
//
//            return true;
//        }
//        else if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == 24){
//            updateMadeStreak(rootView);
//            return true;
//        }
//        else return super.dispatchKeyEvent(event);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
