package com.example.hotstreak.fragments;

import android.os.Bundle;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    BoxStore boxStore;
    Box<Streak> streaksDB;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        boxStore = StreakRepository.getInstance(requireContext());
        streaksDB = boxStore.boxFor(Streak.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return updateTable(inflater, container);
    }

    @Override
    public void onResume() {
        super.onResume();
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup container = getView().findViewById(R.id.frameLayout);
        updateTable(inflater, container);
    }

    public void loadStreakData() {
        streaksDB.getAll();
    }

    public View updateTable(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        TableLayout tableLayout = view.findViewById(R.id.tableLayout);
        tableLayout.removeAllViews();

        // Create header row
        TableRow headerRow = new TableRow(getContext());
        TextView headerBestStreak = configureHistoryTextView(new TextView(getContext()), true);
        TextView headerMadeShots = configureHistoryTextView(new TextView(getContext()), true);
        TextView headerAttemptedShots = configureHistoryTextView(new TextView(getContext()), true);
        headerBestStreak.setText("Best Streak");
        headerMadeShots.setText("Made Shots");
        headerAttemptedShots.setText("Attempted Shots");
        headerRow.addView(headerBestStreak);
        headerRow.addView(headerMadeShots);
        headerRow.addView(headerAttemptedShots);
        tableLayout.addView(headerRow);

        List<Streak> historyItems = streaksDB.getAll();
        for (Streak item : historyItems) {
            TableRow row = new TableRow(getContext());
            TextView bestStreak = configureHistoryTextView(new TextView(getContext()), false);
            TextView madeShots = configureHistoryTextView(new TextView(getContext()), false);
            TextView attemptedShots = configureHistoryTextView(new TextView(getContext()), false);
            bestStreak.setText(String.valueOf(item.getBestStreak()));
            madeShots.setText(String.valueOf(item.getMadeShots()));
            attemptedShots.setText(String.valueOf(item.getAttemptedShots()));
            row.addView(bestStreak);
            row.addView(madeShots);
            row.addView(attemptedShots);
            tableLayout.addView(row);
        }
        return view;
    }

    private TextView configureHistoryTextView(TextView textView, boolean isHeader) {
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        if (isHeader) {
            textView.setBackgroundColor(0xFF23B5F8);
        }
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.LEFT);
        textView.setPadding(3, 3, 3, 3);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        return textView;
    }

}