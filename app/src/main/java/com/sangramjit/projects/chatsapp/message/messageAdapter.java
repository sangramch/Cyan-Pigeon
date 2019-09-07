package com.sangramjit.projects.chatsapp.message;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
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

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.viewHolder> {

    ArrayList<messageItem> messageItems;

    public messageAdapter(Context context, ArrayList<messageItem> messages) {
        messageItems=messages;
        Log.d("chatwindow","in adapter: "+messageItems.size());
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView tvMessage,tvTime,tvSenderID;
        LinearLayout layoutMessage;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage=itemView.findViewById(R.id.tvMessage);
            tvTime=itemView.findViewById(R.id.tvTime);
            tvSenderID=itemView.findViewById(R.id.tvSenderID);
            layoutMessage=itemView.findViewById(R.id.layoutMessage);
        }
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item,parent,false);
        Log.d("chatwindow","viewholder created");
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm a");


        holder.tvMessage.setText(messageItems.get(position).getMessage());
        holder.tvTime.setText(dateFormat.format(Long.parseLong(messageItems.get(position).getTime())));
        holder.tvSenderID.setText(messageItems.get(position).getSenderid());

        if(messageItems.get(position).getSenderid().equals("You")) {
            LinearLayout.LayoutParams param= new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            param.gravity= Gravity.RIGHT;
            holder.layoutMessage.setBackgroundResource(R.drawable.mytext_bg);
            holder.tvMessage.setLayoutParams(param);
            holder.tvTime.setLayoutParams(param);
            holder.tvSenderID.setLayoutParams(param);
            holder.layoutMessage.setLayoutParams(param);

        }
        else if(messageItems.get(position).getSenderid().equals("")){
            LinearLayout.LayoutParams param= new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            param.gravity= Gravity.CENTER;
            holder.layoutMessage.setBackgroundResource(R.color.white);
            holder.tvMessage.setLayoutParams(param);
            holder.tvTime.setLayoutParams(param);
            holder.tvSenderID.setLayoutParams(param);
            holder.layoutMessage.setLayoutParams(param);
        }
        else{
            LinearLayout.LayoutParams param= new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            param.gravity= Gravity.LEFT;
            holder.layoutMessage.setBackgroundResource(R.drawable.rectext_bg);
            holder.tvMessage.setLayoutParams(param);
            holder.tvTime.setLayoutParams(param);
            holder.tvSenderID.setLayoutParams(param);
            holder.layoutMessage.setLayoutParams(param);

        }

        Log.d("chatwindow","viewholder bound");
    }

    @Override
    public int getItemCount() {
        return messageItems.size();
    }
}
