package com.example.mynotesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.TreeSet;

public class NoteEditorActivity extends AppCompatActivity
{
    int noteID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_editor_layout);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("To-do Notes");

        EditText editText=findViewById(R.id.editText);
        editText.setTextIsSelectable(true);

        Intent intent=getIntent();
        noteID=intent.getIntExtra("noteID",-99);
        Log.d("Value passed ",Integer.toString(noteID));

        if(noteID>=0)
        {
            editText.setText(MainActivity.notes.get(noteID));
            Log.d("Notes set up ","true");
        }
        else
        {
            MainActivity.notes.add("");
            noteID=MainActivity.notes.size()-1;
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("Status ","This is before text changes");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try {
                    String str=String.valueOf(charSequence);
                    MainActivity.notes.set(noteID, str);
                    MainActivity.adapter.notifyDataSetChanged();

                    SharedPreferences sharedPreferences = getApplicationContext()
                            .getSharedPreferences("com.example.mynotesapp", Context.MODE_PRIVATE);
                    HashSet<String> hs = new HashSet<>(MainActivity.notes);
                    sharedPreferences.edit().putStringSet("notes", hs).apply();

                    Log.d("Status ", "This is on text change");
                } catch (Exception e) {
                    Log.d("exception","occurred");
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("Status ","This is after text is changed");
            }
        });
    }
}
