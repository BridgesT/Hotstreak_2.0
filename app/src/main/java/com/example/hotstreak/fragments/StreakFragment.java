package com.example.hotstreak.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotstreak.R;
import com.example.hotstreak.Streak;
import com.example.hotstreak.Streak_;
import com.example.hotstreak.repository.StreakRepository;

import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StreakFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StreakFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    int bestStreak, madeStreak=0, attemptStreak=0, currentStreak= 0;
    BoxStore boxStore;
    Box<Streak> streaksDB;

    TextView shotPercentText;
    TextView bestStreakText;
    TextView currentStreakText;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StreakFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StreakFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StreakFragment newInstance(String param1, String param2) {
        StreakFragment fragment = new StreakFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_streak, container, false);
        initializeButtons(view);

        boxStore = StreakRepository.getInstance(requireContext());
        streaksDB = boxStore.boxFor(Streak.class);
        shotPercentText = view.findViewById(R.id.shotPercentText);
        bestStreakText = view.findViewById(R.id.bestStreakText);
        currentStreakText = view.findViewById(R.id.currentStreak);
        // Inflate the layout for this fragment
        return view;
    }

    public void updateMadeStreak(View view) {

        madeStreak++;
        attemptStreak++;
        if (attemptStreak % 100 == 1 && attemptStreak != 1) {
            Toast.makeText(getActivity(), "You shot over " + (attemptStreak - 1) + " shots!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), "You shot over " + (attemptStreak - 1) + " shots!", Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage("Save this streak?");
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    Streak streak = new Streak(bestStreak, madeStreak, attemptStreak);
                    streaksDB.put(streak);
                    dialog.cancel();
                    showConfirmationDialogSaved();
                });

        dialogBuilder.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void loadLastHotstreakData(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage("Load last data?");
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    loadLastData();
                    dialog.cancel();
                });

        dialogBuilder.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        dialogBuilder.setNeutralButton(
                "Delete All",
                (dialog, id) -> {
                    streaksDB.removeAll();
                    dialog.cancel();
                });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void loadLastData() {
        Streak lastStreak = streaksDB.query()
                .orderDesc(Streak_.date)
                .build()
                .findFirst();

        if (lastStreak != null) {
            bestStreak = lastStreak.getBestStreak();
            bestStreakText.setText("Best Streak: " + bestStreak);
            attemptStreak = lastStreak.getAttemptedShots();
            madeStreak = lastStreak.getMadeShots();
            shotPercentText.setText(madeStreak + "/" + attemptStreak);
            currentStreak = 0;
            currentStreakText.setText("Current Streak: " + currentStreak);
            showConfirmationDialogLoaded();
        } else {
            noLoadDataFoundDialog();
        }
    }

    private void showConfirmationDialogSaved() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setMessage("Streak Saved!");
        alertBuilder.setCancelable(true);

        alertBuilder.setNeutralButton("Confirm", (dialog, id) -> {});
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void showConfirmationDialogLoaded() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setMessage("Data loaded!");
        alertBuilder.setCancelable(true);

        alertBuilder.setNeutralButton("Confirm", (dialog, id) -> {});
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void noLoadDataFoundDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setMessage("No previous data found. Save a streak first");
        alertBuilder.setCancelable(true);

        alertBuilder.setNeutralButton("OK", (dialog, id) -> {});
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void initializeButtons(View view) {

        Button madeButton = view.findViewById(R.id.MadeButton);
        madeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMadeStreak(v); // Call the fragment method here
            }
        });

        Button missedButton = view.findViewById(R.id.MissedButton);
        missedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAttemptStreak(v); // Call the fragment method here
            }
        });

        Button saveButton = view.findViewById(R.id.SaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButtonClicked(v); // Call the fragment method here
            }
        });

        Button loadButton = view.findViewById(R.id.LoadButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLastHotstreakData(v); // Call the fragment method here
            }
        });

        Button resetButton = view.findViewById(R.id.ResetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrCancel(v); // Call the fragment method here
            }
        });
    }
}