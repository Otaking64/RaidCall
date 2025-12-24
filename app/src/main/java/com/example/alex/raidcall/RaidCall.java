package com.example.alex.raidcall;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Alex on 6/20/2017.
 */

public class RaidCall extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}