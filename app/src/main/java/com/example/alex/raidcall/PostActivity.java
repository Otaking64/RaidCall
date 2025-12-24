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
import java.util.Locale;

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
        String[] items = new String[]{"Tigerman", "Madam Malachite", "Crossbone Swordmaster", "Tricksy Turtle", "Fiery-Eyed Jimmy", "Black Widow", "Garnet Godwin", "Northern Turtle", "Southern Vermilion", "Eastern Dragon", "Western Tiger"};
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
                // Derive image key from raid name; covers known raids and new wind-direction raids
                ImgUrl = getRaidImageKey(PickedRaid);
                final String Creators_ID = mAuth1.getCurrentUser().getUid();
                final DatabaseReference Raid_ID = mDatabase1.push();
                mDatabaseUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Raid_ID.child("Date").setValue(PickedDate);
                        Raid_ID.child("Boss").setValue(PickedRaid);
                        Raid_ID.child("Time").setValue(Time);
                        Raid_ID.child("Month").setValue(PickedMonth);
                        // Store the local image key instead of a remote URL
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

    /**
     * Map a raid boss name to the corresponding mipmap resource base name.
     * Known bosses are handled explicitly; others fall back to a normalized key.
     */
    private String getRaidImageKey(String bossName) {
        if (bossName == null) return "";
        // Explicit mappings for known assets in mipmap-xxxhdpi (all lowercase)
        if ("Tigerman".equals(bossName)) return "tigerman";
        if ("Madam Malachite".equals(bossName)) return "madammalachite";
        if ("Crossbone Swordmaster".equals(bossName)) return "crossboneswordman";
        if ("Tricksy Turtle".equals(bossName)) return "tricksyturtle";
        if ("Fiery-Eyed Jimmy".equals(bossName)) return "fiery_eyed_jimmy";
        if ("Black Widow".equals(bossName)) return "blackwidow";
        if ("Garnet Godwin".equals(bossName)) return "garnetgodwin";
        if ("Northern Turtle".equals(bossName)) return "northernturtle";
        if ("Southern Vermilion".equals(bossName)) return "southernvermillion";
        if ("Eastern Dragon".equals(bossName)) return "easterndragon";
        if ("Western Tiger".equals(bossName)) return "westerntiger";
        // Fallback for future/new raid names:
        // normalize to lowercase, underscores between words
        String key = bossName.trim().toLowerCase(Locale.US);
        // replace non-alphanumeric with space
        key = key.replaceAll("[^a-z0-9]+", " ");
        key = key.trim().replaceAll("\\s+", "_");
        return key;
    }

}