package com.sangramjit.projects.chatsapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class codeVerificationActivity extends AppCompatActivity {

    Button btnCodeConfirm;
    EditText etCodeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        ActionBar actionbar=getSupportActionBar();
        actionbar.setElevation(0);

        btnCodeConfirm = findViewById(R.id.btnCodeConfirm);
        etCodeInput = findViewById(R.id.etCodeInput);

        btnCodeConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Code=etCodeInput.getText().toString();
                if(!Code.isEmpty()){
                    Code=Code.trim();
                    if(Code.length()==6){
                        Intent returnvalue = new Intent();
                        returnvalue.setData(Uri.parse(Code));
                        setResult(RESULT_OK, returnvalue);
                        finish();
                    }
                }
            }
        });

    }
}
