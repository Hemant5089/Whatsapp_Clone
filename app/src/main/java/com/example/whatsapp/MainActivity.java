package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsapp.Adapter.FragmentAdapter;
import com.example.whatsapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {


     ActivityMainBinding binding;

     FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        binding.viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
             Intent intent = new Intent(MainActivity.this,SettingActivity.class);
             startActivity(intent);
             finish();
        }else if(item.getItemId() == R.id.logout){
            auth.signOut();
            Intent intent = new Intent(MainActivity.this,SignInActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.groupChat){
            Intent intent = new Intent(MainActivity.this,GroupChatActivity.class);
            startActivity(intent);
        }
        return true;
    }
}