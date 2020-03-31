package com.example.intentstester;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Activity2 extends AppCompatActivity {

    TextView tvAct2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        tvAct2=findViewById(R.id.tvAct2);

        String name=getIntent().getStringExtra("name");
        name=name+getString(R.string.conc);
        tvAct2.setText(name);
    }
}
