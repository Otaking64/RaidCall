package com.example.alex.raidcall;

import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.text.SimpleDateFormat;


public class ChatActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private Button SMBtn;
    private EditText MsgField;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private  boolean NameClicked = true;
    private RecyclerView mChatList;
    private DatabaseReference mDatabasePerRaid;

    private FirebaseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        SMBtn = (Button) findViewById(R.id.SendMSGBtn);
        MsgField = (EditText) findViewById(R.id.MessgField);
        final Bundle ChatBundle = getIntent().getExtras();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Chats");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        final String CHID = ChatBundle.getString("Chat_Raid_ID");
        mDatabasePerRaid = FirebaseDatabase.getInstance().getReference().child("Chats").child(CHID);


        mChatList = (RecyclerView)  findViewById(R.id.Message_List);
        mChatList.setHasFixedSize(true);
        mChatList.setLayoutManager(new LinearLayoutManager(this));







        SMBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newMSG = MsgField.getText().toString().trim();
                if (TextUtils.isEmpty(newMSG)) {
                    Toast.makeText(ChatActivity.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ChatBundle != null)
                {
                    final String Chat_Raid_ID = ChatBundle.getString("Chat_Raid_ID");
                    NameClicked = true;
                    if(!TextUtils.isEmpty(newMSG) && NameClicked){
                        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                              String ign = dataSnapshot.child("In game name").getValue().toString().trim();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                                String format = simpleDateFormat.format(new Date());


                                final DatabaseReference MSG_ID = mDatabase.child(Chat_Raid_ID).push();

                                MSG_ID.child("Sender").setValue(ign);
                                MSG_ID.child("Message").setValue(newMSG);
                                MSG_ID.child("Date").setValue(format);

                                Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                                mChatList.smoothScrollToPosition(0);
                                NameClicked = false;
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }else{ Toast.makeText(ChatActivity.this, "Field was empty", Toast.LENGTH_SHORT).show();

                    }
                    MsgField.setText("");
                }else{
                    Toast.makeText(ChatActivity.this, "Something... went wrong", Toast.LENGTH_SHORT).show();
                }


            }
        });




        }
    @Override
    protected void onStart() {

        super.onStart();


        FirebaseRecyclerOptions<Chat> options = new FirebaseRecyclerOptions.Builder<Chat>()
                .setQuery(mDatabasePerRaid, Chat.class)
                .build();

        FirebaseRecyclerAdapter<Chat, ChatMessageHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Chat, ChatMessageHolder>(options) {

                    @NonNull
                    @Override
                    public ChatMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = getLayoutInflater().inflate(R.layout.message_layout, parent, false);
                        return new ChatMessageHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ChatMessageHolder viewHolder,
                                                    int position,
                                                    @NonNull Chat model) {
                        viewHolder.setMessage(model.getMessage());
                        viewHolder.setSender(model.getSender());
                        viewHolder.setDate(model.getDate());
                    }
                };

        firebaseRecyclerAdapter.startListening();
        mChatList.setAdapter(firebaseRecyclerAdapter);





    }

    public static  class ChatMessageHolder extends RecyclerView.ViewHolder{
        View mView;

        public ChatMessageHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }
        public void setMessage(String Message){
            TextView ChatMessage = (TextView) mView.findViewById(R.id.MessageContent);
            ChatMessage.setText(Message);
        }

        public void setSender(String Sender){
            TextView ChatSender = (TextView) mView.findViewById(R.id.MessageSender);
            ChatSender.setText(Sender);

        }

        public void setDate(String Date){
            TextView ChatSender = (TextView) mView.findViewById(R.id.MessageDate);
            ChatSender.setText(Date);

        }

    }

}
