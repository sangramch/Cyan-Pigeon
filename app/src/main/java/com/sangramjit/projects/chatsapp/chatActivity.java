package com.sangramjit.projects.chatsapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sangramjit.projects.chatsapp.chat.chatItem;
import com.sangramjit.projects.chatsapp.message.messageItem;
import com.sangramjit.projects.chatsapp.user.userItem;

import java.util.ArrayList;
import java.util.Date;

public class chatActivity extends AppCompatActivity {

    TextView etMessage;
    Button btnSend;
    chatItem item;
    RecyclerView chatRV;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager lmanager;
    ArrayList<messageItem> messageslist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent=getIntent();
        this.item=(chatItem) intent.getSerializableExtra("chatitem");

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle(item.getTitle());

        etMessage=findViewById(R.id.etMessage);
        btnSend=findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=etMessage.getText().toString().trim();
                if(text!=null){
                    if(text!=""){
                        sendMessage(text);
                        etMessage.setText("");
                    }
                }

            }
        });
    }

    private void loadMessage(){

    }

    private void sendMessage(String text) {
        String key = FirebaseDatabase.getInstance().getReference().child("chat").child(item.getChatID()).push().getKey();
        DatabaseReference messageref=FirebaseDatabase.getInstance().getReference().child("chat").child(item.getChatID());
        messageref.child(key).child("createAt").setValue(new Long(new Date().getTime()).toString());
        messageref.child(key).child("message").setValue(text);
        messageref.child(key).child("senderID").setValue(FirebaseAuth.getInstance().getUid());
    }

    public boolean onOptionsItemSelected(MenuItem Item){
        if (Item.getItemId()==android.R.id.home){
            setResult(RESULT_OK,null);
            finish();
            return true;
        }
        else{
            return super.onOptionsItemSelected(Item);
        }
    }
}
