package com.sangramjit.projects.chatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sangramjit.projects.chatsapp.chat.chatItem;
import com.sangramjit.projects.chatsapp.chat.chatListAdapter;
import com.sangramjit.projects.chatsapp.utils.getCountryCode;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;

public class chatlistActivity extends AppCompatActivity implements chatListAdapter.clickFromAdapter{

    FloatingActionButton toContactsList;
    RecyclerView chatlistRecyclerView;
    RecyclerView.Adapter chatListAdapter;
    RecyclerView.LayoutManager layoutmanager;

    ArrayList<chatItem> chatsarr=new ArrayList<>();

    Map<String, String> contactsmap= new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS,Manifest.permission.READ_CONTACTS},1);
        }

        //Floating action button for user list activity
        toContactsList=findViewById(R.id.fabChatList);
        //Listener for floating action button
        toContactsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoContactsList=new Intent(getApplicationContext(), userlistActivity.class);
                startActivity(gotoContactsList);
            }
        });

        buildContactsMap();

        //Initialise the Recyclerview
        initializeRecyclerView();

        getChats();
    }

    private void buildContactsMap() {

        String countryCode= getCountryCode.getCountryCode(getApplicationContext());
        Cursor phoneNumbers=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);

        while(phoneNumbers.moveToNext()) {
            String name = phoneNumbers.getString(phoneNumbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = phoneNumbers.getString(phoneNumbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            number = number.replace(" ", "");
            number = number.replace("-", "");
            number = number.replace("(", "");
            number = number.replace(")", "");

            if (!String.valueOf(number.charAt(0)).equals("+")) {
                number = countryCode + number;
            }

            contactsmap.put(number,name);
        }
    }


    //get all the chats of this user from the database
    private void getChats() {
        String myUID=FirebaseAuth.getInstance().getUid();

        //to get all the chats
        DatabaseReference chatsdata=FirebaseDatabase.getInstance().getReference().child("user").child(myUID).child("chat");

        loadList(myUID);

        chatsdata.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    String chatID = dataSnapshot.getKey();
                    String uID = dataSnapshot.getValue().toString();

                    DatabaseReference lastmsg=FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);
                    Query query=lastmsg.orderByChild("createAt");

                    query.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                Log.d("cyanpigeon","count: "+dataSnapshot.getChildrenCount());

                                for(DataSnapshot childSnapshot:dataSnapshot.getChildren()){
                                    String message=childSnapshot.child("message").getValue().toString();
                                    String timestamp=childSnapshot.child("createAt").getValue().toString();
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                                    String format = simpleDateFormat.format(Long.parseLong(timestamp));

                                    DatabaseReference refForName=FirebaseDatabase.getInstance().getReference().child("user").child(uID).child("phone");
                                    refForName.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                String Unumber= dataSnapshot.getValue().toString();
                                                String Uname=Unumber;;
                                                if(contactsmap.containsKey(Unumber)) {
                                                    Uname=contactsmap.get(Unumber);
                                                }
                                                chatItem chat = new chatItem(format, message, Uname, chatID);
                                                Log.d("cyanpigeonImp","name: "+Uname+" chatID: "+chatID);

                                                boolean match=false;

                                                for(chatItem item:chatsarr){
                                                    if(item.getChatID().equals(chat.getChatID())){
                                                        match=true;
                                                        break;
                                                    }
                                                }

                                                if(!match) {
                                                    chatsarr.add(chat);
                                                    chatListAdapter.notifyDataSetChanged();
                                                    saveList();
                                                }

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

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

    private void saveList() {
        Log.d("loadlist","in onsave");
        SharedPreferences spref=getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor=spref.edit();
        Gson gson=new Gson();
        Log.d("loadlist","loaded: "+chatsarr.size());
        String serializedChatsArr=gson.toJson(chatsarr);
        editor.putString("chatlist",serializedChatsArr);
        editor.commit();
    }

    private void loadList(String myUID) {
        SharedPreferences spref= getPreferences(MODE_PRIVATE);

        if(spref.contains("chatlist")){
            Gson gson=new Gson();
            String serializedChatsArr=spref.getString("chatlist","");
            Type type=new TypeToken<ArrayList<chatItem>>() {}.getType();
            ArrayList<chatItem> temparr=gson.fromJson(serializedChatsArr,type);
            for(chatItem item:temparr){
                chatsarr.add(item);
            }
            Log.d("loadlist","loaded: "+temparr.size());
            chatListAdapter.notifyDataSetChanged();
        }

        /*else {
            Log.d("loadlist","in loadlist else");
            DatabaseReference chatsdata = FirebaseDatabase.getInstance().getReference().child("user").child(myUID).child("chat");
            chatsdata.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String chatID = childSnapshot.getKey();
                            String uID = childSnapshot.getValue().toString();

                            DatabaseReference lastmsg = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);
                            Query query = lastmsg.orderByChild("createAt");

                            query.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Log.d("cyanpigeon", "count: " + dataSnapshot.getChildrenCount());

                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                            String message = childSnapshot.child("message").getValue().toString();
                                            String timestamp = childSnapshot.child("createAt").getValue().toString();
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                                            String format = simpleDateFormat.format(Long.parseLong(timestamp));

                                            DatabaseReference refForName = FirebaseDatabase.getInstance().getReference().child("user").child(uID).child("phone");
                                            refForName.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        String Unumber = dataSnapshot.getValue().toString();
                                                        String Uname = Unumber;
                                                        ;
                                                        if (contactsmap.containsKey(Unumber)) {
                                                            Uname = contactsmap.get(Unumber);
                                                        }
                                                        chatItem chat = new chatItem(format, message, Uname, chatID);
                                                        Log.d("cyanpigeonImp", "name: " + Uname + " chatID: " + chatID);

                                                        boolean match = false;

                                                        for (chatItem item : chatsarr) {
                                                            if (item.getChatID().equals(chat.getChatID())) {
                                                                match = true;
                                                                break;
                                                            }
                                                        }

                                                        if (!match) {
                                                            chatsarr.add(chat);
                                                        }

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            chatListAdapter.notifyDataSetChanged();
            saveList();
        }*/
    }

    //Create the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //selection function for the menu
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==R.id.mitmLogout){
            FirebaseAuth.getInstance().signOut();
            SharedPreferences spref=getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor=spref.edit();
            editor.clear();
            editor.commit();
            Intent loginscreen = new Intent(getApplicationContext(), loginActivity.class);
            startActivity(loginscreen);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //Recyclerview init function
    private void initializeRecyclerView(){
        chatlistRecyclerView=findViewById(R.id.rvChatList);
        chatlistRecyclerView.setHasFixedSize(true);
        layoutmanager= new LinearLayoutManager(this);
        chatlistRecyclerView.setLayoutManager(layoutmanager);
        chatListAdapter=new chatListAdapter(this,chatsarr);
        chatlistRecyclerView.setAdapter(chatListAdapter);
    }

    @Override
    public void handleClick(chatItem chatitem) {
        //Intent intent = new Intent(getApplicationContext(),chatActivity.class);
        Log.d("adapterclick","clicked");

        Intent intent=new Intent(getApplicationContext(),chatActivity.class);
        intent.putExtra("chatitem",chatitem);
        startActivity(intent);
        Toast.makeText(getApplicationContext(),chatitem.getTitle(),Toast.LENGTH_SHORT).show();
    }
}
