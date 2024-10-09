package com.example.hotstreak;


import androidx.annotation.NonNull;

import java.time.Instant;
import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Streak {

    public Streak() {

    }

    public Streak(int bestStreak, int madeShots, int attemptedShots) {
        this.bestStreak = bestStreak;
        this. madeShots = madeShots;
        this.attemptedShots = attemptedShots;
        this.date = new Date();
    }

    @Id
    private long id; // Unique identifier
    private int bestStreak;
    private int madeShots;
    private int attemptedShots;
    private Date date;

    public long getId() {
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }

    public int getMadeShots() {
        return madeShots;
    }

    public void setMadeShots(int madeShots) {
        this.madeShots = madeShots;
    }

    public int getAttemptedShots() {
        return attemptedShots;
    }

    public void setAttemptedShots(int attemptedShots) {
        this.attemptedShots = attemptedShots;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    @NonNull
    @Override
    public String toString() {
        return "ID: " + id +
                "\nBest Streak: " + bestStreak +
                "\nAttemped Shots: " + attemptedShots +
                "\nMade Shots: " + madeShots;
    }
}
