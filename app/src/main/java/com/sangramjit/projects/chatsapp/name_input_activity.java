package com.sangramjit.projects.chatsapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class name_input_activity extends AppCompatActivity {

    Button btnNameConfirm;
    EditText etNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_input);

        ActionBar actionbar=getSupportActionBar();
        actionbar.setElevation(0);

        btnNameConfirm = findViewById(R.id.btnNameConfirm);
        etNameInput = findViewById(R.id.etNameInput);

        btnNameConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Code=etNameInput.getText().toString();
                if(!Code.isEmpty()){
                Code=Code.trim();
                    Intent returnvalue = new Intent();
                    returnvalue.putExtra("title",Code);
                    setResult(RESULT_OK, returnvalue);
                    finish();
                }
            }
        });

    }
}
