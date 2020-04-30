package com.appdevsoumitri.databasetestersqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // creates or opens the database
        try {
            SQLiteDatabase myDatabase = this.openOrCreateDatabase("Users", MODE_PRIVATE, null);

            myDatabase.execSQL("CREATE TABLE IF NOT EXISTS newUsers (name VARCHAR, age INT(3), id INTEGER PRIMARY KEY)");

            // putting information in the database
            /*myDatabase.execSQL("INSERT INTO newUsers (name,age) VALUES ('Nick',28)");
            myDatabase.execSQL("INSERT INTO newUsers (name,age) VALUES ('Sean',20)");
            myDatabase.execSQL("INSERT INTO newUsers (name,age) VALUES ('John',36)");
            myDatabase.execSQL("INSERT INTO newUsers (name,age) VALUES ('Paul',14)");*/


            // delete from db
            myDatabase.execSQL("DELETE FROM newUsers WHERE id=2 ");

            // polling out data from the db
            Cursor c = myDatabase.rawQuery("SELECT * FROM newUsers", null);

            int nameIndex = c.getColumnIndex("name");
            int ageIndex = c.getColumnIndex("age");
            int idIndex = c.getColumnIndex("id");

            // move cursor to starting position
            c.moveToFirst();

            // loop through the table
            while(c!=null)
            {
                Log.d("name ", c.getString(nameIndex));
                Log.d("age ", c.getString(ageIndex));
                Log.d("id ", c.getString(idIndex));

                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
