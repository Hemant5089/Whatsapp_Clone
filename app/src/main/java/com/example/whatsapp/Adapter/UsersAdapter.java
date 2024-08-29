package com.example.whatsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.ChatDettailActivity;
import com.example.whatsapp.Models.Users;
import com.example.whatsapp.R;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

     ArrayList<Users> list;
     Context context;

     public UsersAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         Users users = list.get(position);
         Picasso.get().load(users.getProfilepic()).placeholder(R.drawable.whatsapp).into(holder.imageView);
         holder.userName.setText(users.getUserName());

        String message = users.getLastMessage();
        if (message == null || message.isEmpty()) {
            holder.lastMessage.setText("No message available");
        } else {
            holder.lastMessage.setText(message);
        }

        FirebaseDatabase.getInstance().getReference().child("chats")
                        .child(FirebaseAuth.getInstance().getUid() + users.getUserId())
                                .orderByChild("timestamp")
                                        .limitToLast(1)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(snapshot.hasChildren()){
                                                             for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                                 holder.lastMessage.setText(dataSnapshot.child("message").getValue().toString());
                                                             }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });





        holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(context, ChatDettailActivity.class);
                 intent.putExtra("userId",users.getUserId());
                 intent.putExtra("profilePic",users.getProfilepic());
                 intent.putExtra("userName",users.getUserName());

                 context.startActivity(intent);
             }
         });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        MaterialTextView userName, lastMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.profileImage);
            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }
}
