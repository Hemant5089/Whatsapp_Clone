package com.example.whatsapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Models.MessageModel;
import com.example.whatsapp.R;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<MessageModel> messageModels;
    Context context;
    String recId;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String recId) {
        this.messageModels = messageModels;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciever, parent, false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               new AlertDialog.Builder(context)
                       .setTitle("Delete")
                       .setMessage("do you want to delete this message")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               FirebaseDatabase database = FirebaseDatabase.getInstance();
                               String senderRoom = FirebaseAuth.getInstance().getUid()+recId;
                               database.getReference().child("chats").child(senderRoom).child(messageModel.getMessageId())
                                       .setValue(null);
                           }
                       }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                           }
                       }).show();


             return false;
           }
       });
        if (holder instanceof SenderViewHolder) {
            ((SenderViewHolder) holder).senderMsg.setText(messageModel.getMessage());
            if (messageModel.getTimestamp() != null) {
                // Format to display hour and minute with AM/PM
                String time = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date(messageModel.getTimestamp()));
                ((SenderViewHolder) holder).senderTime.setText(time);
            }
        } else {
            ((RecieverViewHolder) holder).receiverMsg.setText(messageModel.getMessage());
            if (messageModel.getTimestamp() != null) {
                // Format to display hour and minute with AM/PM
                String time = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date(messageModel.getTimestamp()));
                ((RecieverViewHolder) holder).receiverTime.setText(time);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public static class RecieverViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView receiverMsg, receiverTime;

        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.recieverText);
            receiverTime = itemView.findViewById(R.id.recieverTime);
        }
    }

    public static class SenderViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView senderMsg, senderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
        }
    }
}
