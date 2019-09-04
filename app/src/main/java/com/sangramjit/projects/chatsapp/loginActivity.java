package com.sangramjit.projects.chatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class loginActivity extends AppCompatActivity{

    EditText etNumber;
    Button btnSubmitNumber;
    FirebaseAuth firebaseAuthInstance=FirebaseAuth.getInstance();
    String savedVerificationId;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Log.d("CyanPigeon","Verification Success");
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken);
            Log.d("CyanPigeon","Verification code sent: "+verificationId);
            savedVerificationId=verificationId;
            Intent showCodeVerificationActivity = new Intent(getApplicationContext(),com.sangramjit.projects.chatsapp.codeVerificationActivity.class);
            startActivityForResult(showCodeVerificationActivity,1);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                String code = data.getData().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(savedVerificationId,code);
                signInWithPhoneAuthCredential(credential);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etNumber=findViewById(R.id.etNumber);
        btnSubmitNumber=findViewById(R.id.btnSubmitNumber);

        LoginUser();

        btnSubmitNumber.setOnClickListener(view -> {

            if(!etNumber.getText().toString().isEmpty()) {
                String PhoneNumber=etNumber.getText().toString().trim();
                if(PhoneNumber.length()==10){
                    Log.d("CyanPigeon",  "Phone Number Accepted:: " + PhoneNumber);
                    PhoneNumber="+91" + PhoneNumber;

                    verifyPhoneNumberViaFirebaseAuth(PhoneNumber);
                }
                else{
                    Toast.makeText(getApplicationContext(),"C'mon Mate.. Enter the proper number. You're better than this.",Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"Really? You wanna login without any number you absolute dumbfuck?",Toast.LENGTH_LONG).show();
            }
        });

    }

    void verifyPhoneNumberViaFirebaseAuth(String PhoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                PhoneNumber,
                60,
                TimeUnit.SECONDS,
                loginActivity.this,
                mCallback
        );
    }

    void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential){
        firebaseAuthInstance.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this,new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    final FirebaseUser user = firebaseAuthInstance.getCurrentUser();

                    if(user!=null){
                        final DatabaseReference userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
                        userDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.exists()){
                                    Map<String , Object> userMap = new HashMap<>();
                                    userMap.put("phone",user.getPhoneNumber());
                                    userMap.put("name",user.getPhoneNumber());
                                    userDatabaseRef.updateChildren(userMap);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    Log.d("CyanPigeon","Login Successful");
                    LoginUser();
                }
                else{
                    Log.w("CyanPigeon","Login Failed");
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        Log.w("CyanPigeon","Wrong code was entered");
                        Toast.makeText(getApplicationContext(),"You entered the wrong fucking code genius.",Toast.LENGTH_LONG);
                    }
                }
            }
        });
    }

    void LoginUser(){
        FirebaseUser user = firebaseAuthInstance.getCurrentUser();
        if(user!=null){
            Intent showChatActivity = new Intent(getApplicationContext(),com.sangramjit.projects.chatsapp.chatlistActivity.class);
            startActivity(showChatActivity);
            finish();
            return;
        }
    }
}
