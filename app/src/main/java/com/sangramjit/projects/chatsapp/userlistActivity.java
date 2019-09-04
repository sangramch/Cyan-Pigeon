package com.sangramjit.projects.chatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sangramjit.projects.chatsapp.user.userItem;
import com.sangramjit.projects.chatsapp.user.userlistAdapter;
import com.sangramjit.projects.chatsapp.utils.Iso2Phone;
import com.sangramjit.projects.chatsapp.utils.getCountryCode;

import java.util.ArrayList;

public class userlistActivity extends AppCompatActivity implements userlistAdapter.clickCallback{

    RecyclerView userlistRecyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutmanager;

    ArrayList<userItem> displayarr=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        ActionBar actionbar=getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_userlist);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS,Manifest.permission.READ_CONTACTS},1);
        }


        //Initialise the Recyclerview
        initializeRecyclerView();

        //This function starts populating the actual arraylist
        getContacts();
    }


    //gets the contacts list and extracts name and number from them and then calls a function to see if they are on app
    private void getContacts(){

        String countryCode= getCountryCode.getCountryCode(getApplicationContext());
        Cursor phoneNumbers=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        while(phoneNumbers.moveToNext()){
            String name=phoneNumbers.getString(phoneNumbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number=phoneNumbers.getString(phoneNumbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            number=number.replace(" ","");
            number=number.replace("-","");
            number=number.replace("(","");
            number=number.replace(")","");

            if(!String.valueOf(number.charAt(0)).equals("+")){
                number=countryCode+number;
            }

            Log.d("cyanpigeon","Found number "+number);

            getUserDetails(number,name);
        }
    }


    //checks if number is on app and populates arraylist
    private void getUserDetails(String number,String name) {

        DatabaseReference userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("user");
        Log.i("cyanpigeon","Inside getuserdetails tab");
        String Number=number;
        String uid;
        Query query = userDatabaseRef.orderByChild("phone").equalTo(number);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("cyanpigeon","Inside ondatachange tab");
                if (dataSnapshot.exists()) {
                    Log.d("cyanpigeon","inside exists tab");
                    for(DataSnapshot children : dataSnapshot.getChildren()) {
                        int match = 1;
                        for (userItem chk : displayarr) {
                            if (chk.getNumber().equals(Number)) {
                                match = 0;
                            }
                        }
                        if (match == 1) {
                            if(!children.getKey().equals(FirebaseAuth.getInstance().getUid())) {
                                displayarr.add(new userItem(Number, name, children.getKey()));
                                adapter.notifyDataSetChanged();
                            }
                        }
                        Log.d("cyanpigeon", String.valueOf(displayarr.size()));
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }






    //init for recyclerview
    private void initializeRecyclerView(){
        userlistRecyclerView=findViewById(R.id.rvUserList);
        userlistRecyclerView.setHasFixedSize(true);
        layoutmanager= new GridLayoutManager(this,2);
        userlistRecyclerView.setLayoutManager(layoutmanager);
        adapter=new userlistAdapter(this,displayarr);
        userlistRecyclerView.setAdapter(adapter);
    }

    //For back button clicked
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //for item clicked
    @Override
    public void onItemClicked() {
        Log.i("cyanpigeon", "Function is here");
        finish();
    }
}
