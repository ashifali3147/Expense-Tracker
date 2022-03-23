package dev.webguru.expensetracker2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String SHARED_PREF_NAME = "mysharedpref";
    final String DB_FLAG = "db_flag";
    SharedPreferences dbPrefs;
    Boolean dbExists = false;
    private ArrayList<User> userList = new ArrayList<User>();
    RecyclerView recyclerView;
    CustomAdapter customAdapter;
    FloatingActionButton fab1,fab2,fab3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //        ----------------------------------------FAB Button--------------------------------------
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fab2.getVisibility() != View.VISIBLE|fab3.getVisibility() != View.VISIBLE){
                    fab2.setVisibility(View.VISIBLE);
                    fab2.setClickable(true);
                    fab3.setVisibility(View.VISIBLE);
                    fab3.setClickable(true);
                }
                else{
                    fab2.setVisibility(View.INVISIBLE);
                    fab2.setClickable(false);
                    fab3.setVisibility(View.INVISIBLE);
                    fab3.setClickable(false);

                }
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent debit = new Intent(MainActivity.this, AddUserActivity.class);
                startActivity(debit);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent credit = new Intent(context, CreditPage.class);
//                startActivity(credit);
            }
        });
//        -------------------------------------------------------------------------------
        // get the reference of RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setOnCreateContextMenuListener(this);

        dbPrefs = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        dbExists = dbPrefs.getBoolean(DB_FLAG, false);
        if (!dbExists) {
            //---Create the Database with few dummy data inserted ---
            createDB();
            //---set DB Flag to true---
            SharedPreferences.Editor editor = dbPrefs.edit();
            editor.putBoolean(DB_FLAG, true);
            editor.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        display_rec();
    }

    public void display_rec() {
        fetchUsersFromDB();

        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        customAdapter = new CustomAdapter(MainActivity.this, userList);
        //customAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
    }

    public void fetchUsersFromDB() {
        userList.clear();

        DBAdapter db = new DBAdapter(this);
        //---get all KeyWords---
        db.open();
        Cursor c = db.getAllContacts();
        c.moveToFirst();
        while(!c.isAfterLast()) {
            User u = new User(c.getInt(c.getColumnIndex("_id")), c.getString(c.getColumnIndex("name")), c.getString(c.getColumnIndex("email")));
            userList.add(u);
            c.moveToNext();
        }
        c.close();
        db.close();
    }

    private void createDB() {
        DBAdapter db = new DBAdapter(this);
        db.open();
        db.insertDummyData();
        db.close();
    }

    public void addUser(View v) {
        startActivity(new Intent(this, AddUserActivity.class));
    }

}