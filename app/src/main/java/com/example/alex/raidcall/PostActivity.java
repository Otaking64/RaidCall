package com.example.alex.raidcall;


import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PostActivity extends AppCompatActivity {

    private Button mPostButton;
    private ProgressDialog mProgress;

    private FirebaseAuth mAuth1;
    private DatabaseReference mDatabase1;
    private DatabaseReference mDatabaseUser;

    private FirebaseUser mCurrentUser;



    public DatePicker mdatePicker;
    public TimePicker mtimePicker;
    public Spinner mRaidSelector;


    int hour = 0;
    int min = 0;
    String ImgUrl;


    int currentApiVersion = android.os.Build.VERSION.SDK_INT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mPostButton = (Button) findViewById(R.id.PostButton);
        mdatePicker = (DatePicker) findViewById(R.id.RaidDatePicker) ;
        mtimePicker = (TimePicker) findViewById(R.id.RaidTimePicker);

        mProgress = new ProgressDialog(this);

        mAuth1 = FirebaseAuth.getInstance();
        mCurrentUser = mAuth1.getCurrentUser();

        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Raids");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        final Spinner mRaidSelector = (Spinner) findViewById(R.id.spinner1);
        String[] items = new String[]{"Tigerman", "Madam Malachite", "Crossbone Swordmaster", "Tricksy Turtle", "Fiery-Eyed Jimmy", "Black Widow", "Garnet Godwin", "Northern Turtle", "Southern Vermilion"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        mRaidSelector.setAdapter(adapter);


        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setMessage("Being registered...");
                mProgress.show();

                int PickedDay = mdatePicker.getDayOfMonth();
                final int PickedMonth = mdatePicker.getMonth() + 1;
                int PickedYear = mdatePicker.getYear();

                final String PickedDate =  ( PickedDay + "-" + PickedMonth + "-" + PickedYear);

                if (currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1){
                    hour = mtimePicker.getHour();
                    min = mtimePicker.getMinute();
                } else {
                    hour = mtimePicker.getCurrentHour();
                    min = mtimePicker.getCurrentMinute();
                }
                String hourString;
                if(hour < 10){
                    hourString = "0" + hour;
                }else{
                    hourString = "" + hour;
                }

                String minString;
                if(min < 10){
                    minString = "0" + min;
                }else{
                    minString = "" + min;
                }

                final String Time = (hourString +":"+ minString);

                final String PickedRaid =  mRaidSelector.getSelectedItem().toString();

                if(PickedRaid == "Tigerman"){
                    ImgUrl = "https://firebasestorage.googleapis.com/v0/b/raidcallapi.appspot.com/o/Images%2FTigerman.png?alt=media&token=c91f2d5a-b75a-478c-b49d-b6570e81633d";

                }else if(PickedRaid == "Madam Malachite"){
                    ImgUrl = "https://firebasestorage.googleapis.com/v0/b/raidcallapi.appspot.com/o/Images%2FMadam%20Malachite.fw.png?alt=media&token=1f3d997f-6fbf-4a89-b66b-a264b22483a1";


                }else if(PickedRaid == "Crossbone Swordmaster"){
                    ImgUrl = "https://firebasestorage.googleapis.com/v0/b/raidcallapi.appspot.com/o/Images%2FCrossbone%20Swordmaster.png?alt=media&token=bf2243a8-c456-4c90-9209-fe6015bb9556";
                }else if(PickedRaid == "Tricksy Turtle"){
                    ImgUrl = "https://firebasestorage.googleapis.com/v0/b/raidcallapi.appspot.com/o/Images%2FTricksy%20Turtle.png?alt=media&token=fb6f8626-1389-46be-ab3e-129853cbb211";
                }else if(PickedRaid == "Fiery-Eyed Jimmy"){
                    ImgUrl = "https://firebasestorage.googleapis.com/v0/b/raidcallapi.appspot.com/o/Images%2FFiery-Eyed%20Jimmy.png?alt=media&token=4f01c2cc-ac51-48fc-841e-86b29d8c631c";
                }else if(PickedRaid == "Black Widow"){
                    ImgUrl = "https://firebasestorage.googleapis.com/v0/b/raidcallapi.appspot.com/o/Images%2FBlack%20Widow.png?alt=media&token=c3600936-1f3b-43b4-9aef-17e33168ffed";
                }else if(PickedRaid == "Garnet Godwin"){
                    ImgUrl = "https://firebasestorage.googleapis.com/v0/b/raidcallapi.appspot.com/o/Images%2FGarnet%20Godwin.png?alt=media&token=712b6bb7-8c64-4eb7-9804-698fbc00fac4";
                }else if(PickedRaid == "Northern Turtle"){
                    ImgUrl = "https://firebasestorage.googleapis.com/v0/b/raidcallapi.appspot.com/o/Images%2FNorthern%20Turtle.png?alt=media&token=c69acde8-1538-40cd-bf08-1a1d2b278fab";
                }else if(PickedRaid == "Southern Vermilion"){
                    ImgUrl = "https://firebasestorage.googleapis.com/v0/b/raidcallapi.appspot.com/o/Images%2FSouthern%20Vermillion.png?alt=media&token=7ce0308f-2aa5-4e16-9140-08d64204a8fd";
                }




                final String Creators_ID = mAuth1.getCurrentUser().getUid();
                final DatabaseReference Raid_ID = mDatabase1.push();



                mDatabaseUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Raid_ID.child("Date").setValue(PickedDate);
                        Raid_ID.child("Boss").setValue(PickedRaid);
                        Raid_ID.child("Time").setValue(Time);
                        Raid_ID.child("Month").setValue(PickedMonth);
                        Raid_ID.child("Image").setValue(ImgUrl);
                        Raid_ID.child("Creator's ID").setValue(Creators_ID);
                        Raid_ID.child("IGN").setValue(dataSnapshot.child("In game name").getValue());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });






                mProgress.dismiss();
                Toast.makeText(PostActivity.this, "Posted raid to database.", Toast.LENGTH_SHORT).show();
                Intent Raid2Intent = new Intent(PostActivity.this, RaidActivity.class);
                Raid2Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Raid2Intent);


            }
        });




    }





}