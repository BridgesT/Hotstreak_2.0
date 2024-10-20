package com.example.hotstreak.repository;

import android.content.Context;

import com.example.hotstreak.MyObjectBox;
import com.example.hotstreak.Streak;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;

public class StreakRepository {

    private static BoxStore boxStore;

    // Private constructor to prevent instantiation
    private StreakRepository() {}

    public static synchronized BoxStore getInstance(Context context) {
        if (boxStore == null) {
            // Initialize BoxStore if not already created
            boxStore = MyObjectBox.builder()
                    .androidContext(context.getApplicationContext())
                    .build();
        }
        return boxStore;
    }

    public static void close() {
        if (boxStore != null && !boxStore.isClosed()) {
            boxStore.close();
        }
    }
}
