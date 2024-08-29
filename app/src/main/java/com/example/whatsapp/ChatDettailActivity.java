package com.example.whatsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.whatsapp.Adapter.ChatAdapter;
import com.example.whatsapp.Models.MessageModel;
import com.example.whatsapp.databinding.ActivityChatDettailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDettailActivity extends AppCompatActivity {

    ActivityChatDettailBinding binding;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDettailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        firebaseDatabase = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        //
        final String senderId = auth.getUid();
        String recieveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(ChatDettailActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        });

        final ArrayList<MessageModel> messageModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels,this,recieveId);

        binding.chatRecyclerView.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);



        final String senderRoom = senderId + recieveId;
        final String receiverRoom = recieveId + senderId;

        firebaseDatabase.getReference().child("chats")
                        .child(senderRoom).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                         for(DataSnapshot snapshot1 : snapshot.getChildren()){
                               MessageModel model = snapshot1.getValue(MessageModel.class);
                               model.setMessageId(snapshot1.getKey());
                               messageModels.add(model);
                         }

                         chatAdapter.notifyDataSetChanged();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.typeMessage.getText().toString();
                final MessageModel model = new MessageModel(senderId, message);
                model.setTimestamp(new Date().getTime());
                binding.typeMessage.setText("");

//                // Changes start here
//                DatabaseReference databaseReference = firebaseDatabase.getReference();

                // Save message in sender's chat room
               firebaseDatabase.getReference().child("chats")
                        .child(senderRoom)
                        .push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Chat", "Message sent to sender's room");

                                // Save message in receiver's chat room
                                firebaseDatabase.getReference().child("chats")
                                        .child(receiverRoom)
                                        .push()
                                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("Chat", "Message sent to receiver's room");
                                                // Message successfully saved in both sender's and receiver's rooms
                                            }
                                        });
                            }
                        });
                // Changes end here
            }
        });

        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.whatsapp).into(binding.profileImage);
    }
}