package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsapp.Models.Users;
import com.example.whatsapp.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;


public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding binding;

    ProgressDialog progressDialog;
    FirebaseAuth auth;

    GoogleSignInClient googleSignInClient;

    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();

        // Check if actionBar is not null before hiding
        if (actionBar != null) {
            actionBar.hide();
        }

         auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Login to your account");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.clintId))
                        .requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);


        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.addEmail.getText().toString().isEmpty()){
                     binding.addEmail.setError("Please fill this out");
                     return;
                }
                if(binding.password.getText().toString().isEmpty()){
                    binding.password.setError("Please fill this out");
                    return;
                }
             progressDialog.show();
             auth.signInWithEmailAndPassword(binding.addEmail.getText().toString(),binding.password.getText().toString())
                     .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                              progressDialog.dismiss();

                              if(task.isSuccessful()){
                                  Intent intent = new Intent(SignInActivity.this,MainActivity.class);
                                  startActivity(intent);
                                  finish();
                              }

                              else{
                                  Toast.makeText(SignInActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                              }
                         }
                     });
            }
        });

        if(auth.getCurrentUser() != null) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        binding.clickForSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });


        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    int RC_SIGN_IN = 65;
    private void signIn(){
         Intent signInIntent = googleSignInClient.getSignInIntent();
         startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // google sign in was succussful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("Tag","firebaseAuthWithGoogle:" +account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            }catch (ApiException e){
               // Google sign in failed update ui appropriately
                Log.w("Tag","Google sign in failed",e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Sign in success , update ui with the signed in user's information
                            Log.d("TAG","signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Users users = new Users();
                            users.setUserId(user.getUid());
                            users.setUserName(user.getDisplayName());
                            users.setProfilepic(user.getPhotoUrl().toString());
                            database.getReference().child("Users").child(user.getUid()).setValue(users);
                            Intent intent = new Intent(SignInActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(SignInActivity.this,"Sign in with Google",Toast.LENGTH_SHORT).show();
                            // updateUI(user);
                        }
                        else{
                            //If sign in fails , display a message to the user
                            Log.w("TAG","signInWithCredential:failure",task.getException());
                            // Snackbar.make(mBinding.mainLayout,"Authentication Failed",Snackbar.LENGTH_SHORT).show();
                            // updateUi(null);
                        }
                    }
                });
    }


}