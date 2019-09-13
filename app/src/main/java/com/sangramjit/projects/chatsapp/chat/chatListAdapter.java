package com.sangramjit.projects.chatsapp.chat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangramjit.projects.chatsapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class chatListAdapter extends RecyclerView.Adapter<chatListAdapter.viewHolder>{

    clickFromAdapter activity;

    public interface clickFromAdapter{
        void handleClick(chatItem chatitem);
    }

    ArrayList<chatItem> chats;

    public chatListAdapter(Context context, ArrayList<chatItem> list){
        chats=list;
        activity=(clickFromAdapter) context;
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvTitle,tvLastMessage,tvMessageTime;
        LinearLayout layoutChatListItem;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle=itemView.findViewById(R.id.tvTitle);
            tvLastMessage=itemView.findViewById(R.id.tvLastMessage);
            tvMessageTime=itemView.findViewById(R.id.tvTimeStamp);
            layoutChatListItem=itemView.findViewById(R.id.layoutChatListItem);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("adapterclick","in adapter");
            activity.handleClick((chatItem)view.getTag());
        }
    }





    @NonNull
    @Override
    public chatListAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull chatListAdapter.viewHolder holder, int position) {
        holder.itemView.setTag(chats.get(position));
        holder.tvTitle.setText(chats.get(position).getTitle());
        holder.tvLastMessage.setText(chats.get(position).getLastMessage());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a, dd MMM");
        String format = simpleDateFormat.format(Long.parseLong(chats.get(position).getTimeStamp()));

        holder.tvMessageTime.setText(format);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
