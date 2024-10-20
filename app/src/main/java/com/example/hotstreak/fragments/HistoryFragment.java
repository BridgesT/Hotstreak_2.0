package com.example.hotstreak.fragments;

import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.hotstreak.R;
import com.example.hotstreak.Streak;
import com.example.hotstreak.repository.StreakRepository;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    BoxStore boxStore;
    Box<Streak> streaksDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boxStore = StreakRepository.getInstance(requireContext());
        streaksDB = boxStore.boxFor(Streak.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment's layout
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Update the table with new data each time the fragment is resumed
        updateTable();
    }

    // Method to load data and update the TableLayout
    public void updateTable() {
        View view = getView(); // Get the current view
        if (view != null) {
            TableLayout tableLayout = view.findViewById(R.id.tableLayout);
            tableLayout.removeAllViews(); // Clear old data

            // Create and add the header row
            TableRow headerRow = new TableRow(getContext());
            TextView headerBestStreak = configureHistoryTextView(new TextView(getContext()), true);
            TextView headerMadeShots = configureHistoryTextView(new TextView(getContext()), true);
            TextView headerAttemptedShots = configureHistoryTextView(new TextView(getContext()), true);
            TextView headerShotPercentage = configureHistoryTextView(new TextView(getContext()), true);
            headerBestStreak.setText("Best Streak");
            headerMadeShots.setText("Made Shots");
            headerAttemptedShots.setText("Attempted Shots");
            headerShotPercentage.setText("Shot %");
            headerRow.addView(headerBestStreak);
            headerRow.addView(headerMadeShots);
            headerRow.addView(headerAttemptedShots);
            headerRow.addView(headerShotPercentage);
            tableLayout.addView(headerRow);

            // Fetch the latest streak data
            List<Streak> historyItems = streaksDB.getAll();
            historyItems.sort((streak1, streak2) -> Long.compare(streak2.getId(), streak1.getId()));

            // Populate the table with data from the database
            for (Streak item : historyItems) {
                TableRow row = new TableRow(getContext());
                TextView bestStreak = configureHistoryTextView(new TextView(getContext()), false);
                TextView madeShots = configureHistoryTextView(new TextView(getContext()), false);
                TextView attemptedShots = configureHistoryTextView(new TextView(getContext()), false);
                TextView shotPercentage = configureHistoryTextView(new TextView(getContext()), false);
                bestStreak.setText(String.valueOf(item.getBestStreak()));
                madeShots.setText(String.valueOf(item.getMadeShots()));
                attemptedShots.setText(String.valueOf(item.getAttemptedShots()));
                shotPercentage.setText(item.getMadeShots() == 0 ? "0.0%" : getShotPercentage(item));
                row.addView(bestStreak);
                row.addView(madeShots);
                row.addView(attemptedShots);
                row.addView(shotPercentage);
                tableLayout.addView(row);
            }
        }
    }

    @NonNull
    private static String getShotPercentage(Streak item) {
        return String.format("%.2f", (double) item.getMadeShots() / item.getAttemptedShots() * 100) + "%";
    }

    // Method to configure the TextView for the table
    private TextView configureHistoryTextView(TextView textView, boolean isHeader) {
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        if (isHeader) {
            textView.setBackgroundColor(0xFF23B5F8); // Set header background color
        }
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.LEFT);
        textView.setPadding(3, 3, 3, 3);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        if (!isHeader) {
            // Create a border drawable
            ShapeDrawable border = new ShapeDrawable(new RectShape());
            border.getPaint().setColor(0xFF000000); // Border color
            border.getPaint().setStyle(Paint.Style.STROKE);
            border.getPaint().setStrokeWidth(2); // Border width

            // Set the border as the background
            textView.setBackground(border);
        }
        return textView;
    }
}