package com.example.hotstreak;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import io.objectbox.Box;
import io.objectbox.BoxStore;

public class MainActivity extends AppCompatActivity {

    int bestStreak, madeStreak=0, attemptStreak=0, currentStreak= 0;
    BoxStore boxStore;
    Box<Streak> streaksDB;

    TextView shotPercentText;
    TextView bestStreakText;
    TextView currentStreakText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boxStore = MyObjectBox.builder().androidContext(this).build();
        streaksDB = boxStore.boxFor(Streak.class);
        shotPercentText = findViewById(R.id.shotPercentText);
        bestStreakText = findViewById(R.id.bestStreakText);
        currentStreakText = findViewById(R.id.currentStreak);
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        View rootView = findViewById(android.R.id.content).getRootView();

        if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == 66){

            //Toast.makeText(MainActivity.this, "pressed enter", Toast.LENGTH_SHORT).show();
            updateAttemptStreak(rootView);

            return true;
        }
        else if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == 24){
            updateMadeStreak(rootView);
            return true;
        }
        else return super.dispatchKeyEvent(event);
    }

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

    public void updateMadeStreak(View view) {

        madeStreak++;
        attemptStreak++;
        if (attemptStreak % 100 == 1 && attemptStreak != 1) {
            Toast.makeText(MainActivity.this, "You shot over " + (attemptStreak - 1) + " shots!", Toast.LENGTH_SHORT).show();
        }

        if(currentStreak >= bestStreak) {
            bestStreak++;
        }
        currentStreak++;
        shotPercentText.setText(madeStreak + "/" + attemptStreak);
        bestStreakText.setText("Best Streak: "+ bestStreak);
        currentStreakText.setText("Current Streak: " + currentStreak);

    }
    public void updateAttemptStreak(View view) {

        attemptStreak++;
        if (attemptStreak % 100 == 1 && attemptStreak != 1) {
            Toast.makeText(MainActivity.this, "You shot over " + (attemptStreak - 1) + " shots!", Toast.LENGTH_SHORT).show();
        }
        currentStreak = 0;
        currentStreakText.setText("Current Streak: 0");
        shotPercentText.setText(madeStreak + "/" + attemptStreak);
    }

    public void resetAllViews(View view) {
        attemptStreak = 0;
        currentStreak = 0;
        madeStreak = 0;
        bestStreak = 0;
        shotPercentText.setText(madeStreak + "/" + attemptStreak);
        bestStreakText.setText("Best Streak: "+ bestStreak);
        currentStreakText.setText("Current Streak: " + currentStreak);
    }

    public void confirmOrCancel(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        dialogBuilder.setMessage("Are you sure you want to reset?");
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    resetAllViews(view);
                    dialog.cancel();
                });

        dialogBuilder.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void saveButtonClicked(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        dialogBuilder.setMessage("Save this streak?");
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    Streak streak = new Streak(bestStreak, madeStreak, attemptStreak);
                    streaksDB.put(streak);
                    dialog.cancel();
                    showConfirmationDialog();
                });

        dialogBuilder.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());


        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void loadLastHotstreakData(View view) {
        Streak lastStreak = streaksDB.query()
                .orderDesc(Streak_.date) // Order by ID (or use Streak_.timestamp for timestamp)
                .build()
                .findFirst(); // Get the first result after sorting

        if (lastStreak != null) {
            bestStreak = lastStreak.getBestStreak();
            bestStreakText.setText("Best Streak: " + bestStreak);
            attemptStreak = lastStreak.getAttemptedShots();
            madeStreak = lastStreak.getMadeShots();
            shotPercentText.setText(madeStreak + "/" + attemptStreak);
            currentStreak = 0;
            currentStreakText.setText("Current Streak: " + currentStreak);
        } else {
            noLoadDataFoundDialog();
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Streak Saved!");
        alertBuilder.setCancelable(true);

        alertBuilder.setNeutralButton("Confirm", (dialog, id) -> {});
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void noLoadDataFoundDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("No previous data found. Save a streak first");
        alertBuilder.setCancelable(true);

        alertBuilder.setNeutralButton("OK", (dialog, id) -> {});
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
}
