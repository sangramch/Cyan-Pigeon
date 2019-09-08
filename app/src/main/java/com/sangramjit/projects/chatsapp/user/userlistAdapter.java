package com.sangramjit.projects.chatsapp.user;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.sangramjit.projects.chatsapp.R;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class userlistAdapter extends RecyclerView.Adapter<userlistAdapter.viewHolder>{

    ArrayList<userItem> chats;
    clickCallback activity;


    public interface clickCallback{
        void onItemClicked();
    }


    public userlistAdapter(Context context, ArrayList<userItem> list){
        chats=list;
        activity=(clickCallback) context;
    }



    public class viewHolder extends RecyclerView.ViewHolder{

        TextView tvName,tvNumber;
        LinearLayout layoutUserListItem;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tvName);
            tvNumber=itemView.findViewById(R.id.tvNumber);
            layoutUserListItem=itemView.findViewById(R.id.layoutUserListItem);
        }
    }



    @NonNull
    @Override
    public userlistAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull userlistAdapter.viewHolder holder, int position) {
        holder.itemView.setTag(chats.get(position));
        holder.tvName.setText(chats.get(position).getName());
        holder.tvNumber.setText(chats.get(position).getNumber());
        holder.layoutUserListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sendtime = new Long(new Date().getTime()).toString();

                String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(chats.get(position).getUID());
                FirebaseDatabase.getInstance().getReference().child("user").child(chats.get(position).getUID()).child("chat").child(key).setValue(FirebaseAuth.getInstance().getUid());

                FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("0").child("createAt").setValue(sendtime);
                FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("0").child("message").setValue("You are now Connected");
                FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("0").child("senderID").setValue("0");


                activity.onItemClicked();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
