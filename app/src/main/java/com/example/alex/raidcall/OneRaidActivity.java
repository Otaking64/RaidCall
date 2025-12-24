package com.example.alex.raidcall;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OneRaidActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseJoin;
    private DatabaseReference mDatabaseRaids;
    private ListView mNameList;
    private FirebaseAuth mAuth;
    private Button RmvRaidButton;
    boolean Poster = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_raid2);
        final String Raid_ID = getIntent().getExtras().getString("Raid_Key");
        mDatabaseJoin = FirebaseDatabase.getInstance().getReference().child("Joins");
        mDatabaseRaids = FirebaseDatabase.getInstance().getReference().child("Raids");
        RmvRaidButton = (Button) findViewById(R.id.RmvRaidButton);

        mAuth = FirebaseAuth.getInstance();
       mNameList = (ListView) findViewById(R.id.Name_List);

        FirebaseListOptions<String> options = new FirebaseListOptions.Builder<String>()
                .setLayout(android.R.layout.simple_list_item_1)
                .setQuery(mDatabaseJoin.child(Raid_ID), String.class)
                .build();

        FirebaseListAdapter<String> firebaseListAdapter = new FirebaseListAdapter<String>(options) {
            @Override
            protected void populateView(View v, String model, int position) {


                TextView textView = (TextView) v.findViewById(android.R.id.text1);
                textView.setText(model);
            }
        };

        mNameList.setAdapter(firebaseListAdapter);

        mDatabaseRaids.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if((mAuth.getCurrentUser().getUid()).equals(dataSnapshot.child(Raid_ID).child("Creator's ID").getValue().toString())){



                    RmvRaidButton.setVisibility(View.VISIBLE);
                    Poster = true;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        RmvRaidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Raid_ID = getIntent().getExtras().getString("Raid_Key");
                AlertDialog.Builder rmvdial = new AlertDialog.Builder(OneRaidActivity.this);
                rmvdial.setMessage("Are you sure you want to remove this raid?").setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(Poster = true){
                                    mDatabaseRaids.child(Raid_ID).removeValue();
                                    mDatabaseJoin.child(Raid_ID).removeValue();

                                    Intent mainIntent = new Intent (OneRaidActivity.this, RaidActivity.class);
                                    startActivity(mainIntent);
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog remove = rmvdial.create();
                remove.setTitle("Remove raid");
                remove.show();

            }
        });





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.oneraid_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Bundle ChatBundle = new Bundle();

        if(item.getItemId()== R.id.ChatBTN){
            final String Chat_ID = getIntent().getExtras().getString("Raid_Key");
            ChatBundle.putString("Chat_Raid_ID", Chat_ID );
            Intent ChatIntent = new Intent(getApplicationContext(), ChatActivity.class);
            ChatIntent.putExtras(ChatBundle);


            startActivity(ChatIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
