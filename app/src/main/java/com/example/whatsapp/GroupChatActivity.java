package com.example.whatsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.whatsapp.Adapter.ChatAdapter;
import com.example.whatsapp.Models.MessageModel;
import com.example.whatsapp.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {
    ActivityGroupChatBinding binding;
    FirebaseDatabase database;
    ChatAdapter adapter;
    ArrayList<MessageModel> messageModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChatActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        database = FirebaseDatabase.getInstance();
        messageModels = new ArrayList<>();
        adapter = new ChatAdapter(messageModels, this);
        binding.chatRecyclerView.setAdapter(adapter);
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load chat messages when the activity is created
        loadChatMessages();

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = binding.typeMessage.getText().toString();
                final String senderId = FirebaseAuth.getInstance().getUid();
                final MessageModel model = new MessageModel(senderId, message);
                model.setTimestamp(new Date().getTime());
                binding.typeMessage.setText("");

                database.getReference().child("Group Chat")
                        .push()
                        .setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Message sent successfully
                            }
                        });
            }
        });
    }

    private void loadChatMessages() {
        database.getReference().child("Group Chat").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModels.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel model = dataSnapshot.getValue(MessageModel.class);
                    messageModels.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }
}