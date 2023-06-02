package com.example.renthubpablo.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.renthubpablo.R;

public class NoAppStatus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_no_app_status);
        TextView errorMessage = findViewById(R.id.tvInfoNoStatus);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("errorMessage")) {
            errorMessage.setText(intent.getStringExtra("errorMessage"));
        }
    }
}