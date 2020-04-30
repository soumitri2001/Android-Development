package com.example.mynotesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> notes = new ArrayList<>();
    ArrayList<String> headers=new ArrayList<>();
    static ArrayAdapter adapter;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("TODO List");

        sharedPreferences=getApplicationContext()
                .getSharedPreferences("com.example.mynotesapp", Context.MODE_PRIVATE);

        ListView listView=findViewById(R.id.listView);

        HashSet<String> set=(HashSet<String>) sharedPreferences.getStringSet("notes",new HashSet<String>());
        if(set.size()==0) {
            // no notes present as yet
            notes.add("Sample Note 1");
            Log.d("status","added default note");
        } else {
            notes=new ArrayList<>(set);
        }

        /*try{
            for(String s:notes)
            {
                String str="";
                if(!s.equals(null))
                {
                    int p=Math.min(s.indexOf('\n'),s.lastIndexOf(' '));
                    str=s.substring(0,p);
                }
                headers.add(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Status ", "Exception thrown");
        }*/

        adapter=new ArrayAdapter(this, android.R.layout.simple_list_item_1,notes);
        listView.setAdapter(adapter);

        Log.d("List view setup ","true");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int idx, long l) {
                Log.d("Index of item clicked",Integer.toString(idx));

                Intent intent=new Intent(getApplicationContext(),NoteEditorActivity.class);
                intent.putExtra("noteID",idx);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int i, long l) {

                final int itemDel=i;

                Log.d("Selected item ",Integer.toString(itemDel));
                // AlertDialog to ask if user wants to delete the item
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                notes.remove(itemDel); // headers.remove(itemDel);
                                adapter.notifyDataSetChanged();

                                HashSet<String> hs=new HashSet<>(MainActivity.notes);
                                sharedPreferences.edit().putStringSet("notes",hs).apply();

                                Log.d("Deleted ",Integer.toString(itemDel));
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();


                return true; // this allows a long click to happen
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.add_note:
                Log.d("clicked ","Add new note");
                Intent intent=new Intent(getApplicationContext(),NoteEditorActivity.class);
                startActivity(intent);
                break;
            case R.id.github:
                Log.d("clicked ","visit github");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/soumitri2001")));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
