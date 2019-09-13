package com.sangramjit.projects.chatsapp.utils;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sangramjit.projects.chatsapp.R;

import java.util.HashMap;

public class messageListenerService extends Service {


    String CHANNEL_ID="MessageNotificationChannel";
    int NotCount=0;

    HashMap contactslist;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("IntentCreation","Intent Created");



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        contactslist=(HashMap) intent.getSerializableExtra("contactsmap");

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("chat");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()) {

                    Log.d("new_message", "inside ondatachange");
                    DatabaseReference chatmine = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chat").child(dataSnapshot.getKey());
                    chatmine.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                DatabaseReference findmessage = FirebaseDatabase.getInstance().getReference().child("chat").child(dataSnapshot.getKey());
                                Query query;


                                SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                                if(spref.contains(dataSnapshot.getKey())){
                                    Log.d("new_message","found key in spref");
                                    String lastrecieved=spref.getString(dataSnapshot.getKey(),"");
                                    query=findmessage.orderByChild("createAt").startAt(lastrecieved);
                                }
                                else{
                                    Log.d("new_message","did not find key in spred");
                                    query=findmessage.orderByChild("createAt");
                                }



                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){

                                            for(DataSnapshot childSnapshot:dataSnapshot.getChildren()){
                                                String UID=childSnapshot.child("senderID").getValue().toString();
                                                if(!UID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                                                    DatabaseReference senderref=FirebaseDatabase.getInstance().getReference()
                                                            .child("user").child(UID).child("phone");

                                                    senderref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if(dataSnapshot.exists()){
                                                                String number=dataSnapshot.getValue().toString();
                                                                String title;
                                                                if(contactslist.containsKey(number)){
                                                                    title=contactslist.get(number).toString();
                                                                }
                                                                else{
                                                                    title=number;
                                                                }
                                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID )
                                                                        .setSmallIcon(R.drawable.person)
                                                                        .setContentTitle(title)
                                                                        .setContentText(childSnapshot.child("message").getValue().toString())
                                                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                                                NotificationManagerCompat notificationManager=NotificationManagerCompat.from(getApplicationContext());
                                                                notificationManager.notify(NotCount,builder.build());
                                                                NotCount++;
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                Log.d("new_message","changed "+dataSnapshot.getKey());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
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
        return super.onStartCommand(intent, flags, startId);
    }
}
