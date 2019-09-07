package com.sangramjit.projects.chatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sangramjit.projects.chatsapp.chat.chatItem;
import com.sangramjit.projects.chatsapp.message.messageAdapter;
import com.sangramjit.projects.chatsapp.message.messageItem;
import com.sangramjit.projects.chatsapp.user.userItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class chatActivity extends AppCompatActivity {

    TextView etMessage;
    ImageButton btnSend;
    chatItem item;
    RecyclerView rvMessages;
    RecyclerView.Adapter adapter;
    LinearLayoutManager lmanager;
    ArrayList<messageItem> messageslist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);


        ActionBar actionbar = getSupportActionBar();
        actionbar.setElevation(0);
        actionbar.setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        this.item=(chatItem) intent.getSerializableExtra("chatitem");

        actionbar.setTitle(item.getTitle());

        etMessage=findViewById(R.id.etMessage);
        btnSend=findViewById(R.id.btnSend);

        initializeRecyclerView();

        loadMessage();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=etMessage.getText().toString().trim();
                Log.d("text_sent",text);
                if(text!=null){
                    if(!text.equals("")){
                        sendMessage(text);
                        etMessage.setText("");
                    }
                }

            }
        });
    }

    private void initializeRecyclerView() {
        rvMessages=findViewById(R.id.rvmessages);
        rvMessages.setHasFixedSize(true);
        lmanager= new LinearLayoutManager(this);
        lmanager.setOrientation(RecyclerView.VERTICAL);
        lmanager.setStackFromEnd(true);
        rvMessages.setLayoutManager(lmanager);
        adapter=new messageAdapter(this,messageslist);
        rvMessages.setAdapter(adapter);

    }

    private void loadMessage(){

        DatabaseReference messageload=FirebaseDatabase.getInstance().getReference().child("chat").child(item.getChatID());
        Query query=messageload.orderByChild("createAt");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    Object message=dataSnapshot.child("message").getValue();
                    Object time=dataSnapshot.child("createAt").getValue();
                    Object senderid=dataSnapshot.child("senderID").getValue();
                    if(message!=null && time!=null && senderid!=null) {
                        String user;

                        if(senderid.toString().equals(FirebaseAuth.getInstance().getUid())){
                            user="You";
                        }
                        else if(senderid.toString().equals("0")){
                            user="";
                        }
                        else{
                            user=item.getTitle();
                        }

                        messageslist.add(new messageItem(message.toString(),time.toString(),user));
                        Log.d("msgaddchat","Message Added"+message.toString());
                        Log.d("chatwindow","onchildadded: "+messageslist.size());
                    }
                    setResult(RESULT_OK,null);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String text) {
        String key = FirebaseDatabase.getInstance().getReference().child("chat").child(item.getChatID()).push().getKey();
        DatabaseReference messageref=FirebaseDatabase.getInstance().getReference().child("chat").child(item.getChatID());

        Map<String, String> sender=new HashMap<>();
        sender.put("createAt",new Long(new Date().getTime()).toString());
        sender.put("message",text);
        sender.put("senderID",FirebaseAuth.getInstance().getUid());

        messageref.child(key).setValue(sender);
    }

    public boolean onOptionsItemSelected(MenuItem Item){
        if (Item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        else{
            return super.onOptionsItemSelected(Item);
        }
    }
}
