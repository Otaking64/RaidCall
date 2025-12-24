package com.example.alex.raidcall;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import android.util.Log;

public class RaidActivity extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private RecyclerView mRaidList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseJoin;
    private DatabaseReference mDatabaseCounts;
    private DatabaseReference mDatabaseUser;
    private boolean mProcessJoin = false;
    private boolean mCountClicker = false;
    private boolean mAccountClicker = false;
    private boolean mNotified = false;
    BufferedReader reader;
    RequestQueue rq;
    TextView TZtext;
    String TimeZone;
    String url ="https://raidcallapi.firebaseio.com/TimeZone.json";
    NotificationCompat.Builder notification;
    int NOID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raid);
        rq = Volley.newRequestQueue(this);
        TZtext = (TextView) findViewById(R.id.TZtext);
        SendJasonRequest();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Raids");
        mDatabase.keepSynced(true);
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mRaidList = (RecyclerView) findViewById(R.id.RaidList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mRaidList.setHasFixedSize(true);
        mRaidList.setLayoutManager(layoutManager);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(RaidActivity.this, MainActivity.class));
                }
            }
        };
        mDatabaseJoin = FirebaseDatabase.getInstance().getReference().child("Joins");
        mDatabaseCounts = FirebaseDatabase.getInstance().getReference().child("Joins");
        mDatabaseJoin.keepSynced(true);
        mDatabaseCounts.keepSynced(true);
    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerOptions<Raid> options = new FirebaseRecyclerOptions.Builder<Raid>()
                .setQuery(mDatabase, Raid.class)
                .build();
        FirebaseRecyclerAdapter<Raid,RaidViewHolder> FirebaseRecycleHandler = new FirebaseRecyclerAdapter<Raid, RaidViewHolder>(options) {
            @NonNull
            @Override
            public RaidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.raid_row, parent, false);
                return new RaidViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull RaidViewHolder viewHolder, int position, @NonNull Raid model) {
                final String Raid_Key = getRef(position).getKey();
                viewHolder.setBoss(model.getBoss());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setImage(getApplicationContext(),  model.getImage());
                viewHolder.setIGN(model.getIGN());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabaseJoin.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                assert Raid_Key != null;
                                if(dataSnapshot.child(Raid_Key).hasChildren()){
                                    Intent OneRaidIntent = new Intent(RaidActivity.this, OneRaidActivity.class);
                                    OneRaidIntent.putExtra("Raid_Key", Raid_Key);
                                    startActivity(OneRaidIntent);
                                } else{
                                    Toast.makeText(RaidActivity.this, ("No one joined this Raid yet."), Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                });
                viewHolder.mJoinBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessJoin = true;
                        mDatabaseJoin.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (mProcessJoin) {
                                    assert Raid_Key != null;
                                    assert mAuth.getCurrentUser() != null;
                                    if (dataSnapshot.child(Raid_Key).hasChild(mAuth.getCurrentUser().getUid())) {
                                        Toast.makeText(RaidActivity.this, ("You already joined the raid."), Toast.LENGTH_SHORT).show();
                                        mProcessJoin = false;
                                    } else {
                                        mDatabaseUser.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String UserValue =  dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("In game name").getValue().toString();
                                                mDatabaseJoin.child(Raid_Key).child(mAuth.getCurrentUser().getUid()).setValue(UserValue);
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                        Toast.makeText(RaidActivity.this, ("You joined the raid."), Toast.LENGTH_SHORT).show();
                                        mProcessJoin = false;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                });
            }
        };
        FirebaseRecycleHandler.startListening();
        mRaidList.setAdapter(FirebaseRecycleHandler);
    }

    public static class RaidViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Button mJoinBtn;
        public RaidViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mJoinBtn = (Button) mView.findViewById(R.id.Joinbtn);
        }
        public void setBoss(String Boss){
            TextView Raid_Boss = (TextView) mView.findViewById(R.id.RaidTitle);
            Raid_Boss.setText(Boss);
        }
        public void setDate(String Date){
            TextView Raid_Date = (TextView) mView.findViewById(R.id.DateOfRaid);
            Raid_Date.setText(Date);
        }
        public void setTime(String Time){
            TextView Raid_Time = (TextView) mView.findViewById(R.id.TimeOfRaid);
            Raid_Time.setText(Time);
        }
        public void setImage(Context ctx, String Image){
            ImageView Raid_Image = (ImageView) mView.findViewById(R.id.RaidImage);
            // Treat Image as a local resource name like "tigerman" that may live in mipmap or drawable
            if (Image != null && !Image.isEmpty()) {
                int resId = ctx.getResources().getIdentifier(Image, "mipmap", ctx.getPackageName());
                if (resId == 0) {
                    // Fallback to drawable if not found in mipmap
                    resId = ctx.getResources().getIdentifier(Image, "drawable", ctx.getPackageName());
                }
                if (resId != 0) {
                    Raid_Image.setImageResource(resId);
                } else {
                    Log.w("RaidActivity", "No resource found for image key: " + Image);
                    // Optional: set a default image if the key is invalid
                    // Raid_Image.setImageResource(R.drawable.raid_default);
                }
            }
        }
        public void setIGN(String IGN){
            TextView IGN_Raid = (TextView) mView.findViewById(R.id.RaidIGN);
            IGN_Raid.setText(IGN);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            startActivity(new Intent(RaidActivity.this, PostActivity.class));
        } else if(item.getItemId() == R.id.action_settings){
            final Bundle accBundle = new Bundle();
            mAccountClicker = true;
            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (mAccountClicker){
                        String mAccName = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("Name").getValue().toString();
                        String mAccIGN = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("In game name").getValue().toString();
                        String mAccEmail =  dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("Email").getValue().toString();
                        System.out.println(mAccEmail);
                        accBundle.putString("bamName", mAccName);
                        accBundle.putString("bamIGN", mAccIGN);
                        accBundle.putString("bamEmail", mAccEmail);
                        Intent accintent = new Intent(getApplicationContext(), AccountActivity.class);
                        accintent.putExtras(accBundle);
                        startActivity(accintent);
                        mAccountClicker = false;
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    public void SendJasonRequest(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    TimeZone = response.getString("TZ");
                    TZtext.setText("All times are in: " + TimeZone);
                    System.out.println(TimeZone);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        rq.add(jsonObjectRequest);
    }
}
