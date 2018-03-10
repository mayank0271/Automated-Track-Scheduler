package com.aloofwillow96.rangebar;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.appyvet.rangebar.IRangeBarFormatter;
import com.appyvet.rangebar.RangeBar;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FacultyActivity extends AppCompatActivity {
    RangeBar seekBar;
    TextView leftTime;
    TextView rightTIme;
    ListView freeTimeList;
    ArrayList<String> myFreeTimeList;
    ArrayAdapter<String> arrayAdapter;

    FirebaseAuth facultyAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseDatabase facultyDatabase;
    DatabaseReference facultyDatabaseReference;
    final int RC_SIGN_IN = 1;
    Faculty faculty;
    String username, userId;
    static ArrayList<FacultyTime> finalTimelist;

    ChildEventListener childEventlistener;
    ArrayList<Faculty> facultyArrayList;


    int getCurrentHoursMins() {
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("mm");


        String hr = dateFormat2.format(System.currentTimeMillis());
        String min = dateFormat3.format(System.currentTimeMillis());

        int hourmins = Integer.parseInt(min) + (Integer.parseInt(hr) * 60);
        return hourmins;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu: AuthUI.getInstance().signOut(this);
                break;
            case R.id.requests:Intent intent=new Intent(this,RequestsActivity.class);
                               startActivity(intent);

        }
        return super.onOptionsItemSelected(item);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        facultyAuth = FirebaseAuth.getInstance();
        faculty = new Faculty();
        facultyDatabase = FirebaseDatabase.getInstance();
        facultyDatabaseReference = facultyDatabase.getReference().child("faculty");
        facultyArrayList = new ArrayList<>();


        seekBar = (RangeBar) findViewById(R.id.seekbar);

        leftTime = (TextView) findViewById(R.id.leftTime);

        rightTIme = (TextView) findViewById(R.id.rightTime);

        freeTimeList = (ListView) findViewById(R.id.listView);
        myFreeTimeList = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, myFreeTimeList);
        freeTimeList.setAdapter(arrayAdapter);



        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm");
        String currentTime = dateFormat1.format(System.currentTimeMillis());
        leftTime.setText(currentTime);


        int hourMinSecs = getCurrentHoursMins();

        seekBar.setTickEnd((float) hourMinSecs + 600);
        seekBar.setTickStart((float) hourMinSecs);
        seekBar.setTickInterval(1);
        seekBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(final RangeBar rangeBar, final int leftPinIndex, final int rightPinIndex, final String leftPinValue, final String rightPinValue) {
                rangeBar.setFormatter(new IRangeBarFormatter() {
                    @Override
                    public String format(String value) {

                        int valueInt = Integer.parseInt(value);
                        int timeDiff = valueInt - getCurrentHoursMins();
                        Calendar now = Calendar.getInstance();
                        Calendar nowRight = Calendar.getInstance();
                        Calendar nowLeft = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                        if (timeDiff > 0) {
                            now.add(Calendar.MINUTE, timeDiff);
                            value = df.format(now.getTime());
                        } else
                            value = df.format(System.currentTimeMillis());

                        int rTimeDiff = Integer.parseInt(rightPinValue) - getCurrentHoursMins();
                        int lTimeDiff = Integer.parseInt(leftPinValue) - getCurrentHoursMins();

                        if (rTimeDiff > 0) {
                            nowRight.add(Calendar.MINUTE, rTimeDiff);
                            rightTIme.setText(df.format(nowRight.getTime()));
                        } else if (rTimeDiff <= 0)
                            rightTIme.setText(df.format(System.currentTimeMillis()));


                        if (lTimeDiff > 0) {
                            nowLeft.add(Calendar.MINUTE, lTimeDiff);
                            leftTime.setText(df.format(nowLeft.getTime()));
                        } else if (lTimeDiff <= 0)
                            leftTime.setText(df.format(System.currentTimeMillis()));

                        return value;
                    }
                });
            }
        });
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(FacultyActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
                    //onSignedIn();
                    userId=FirebaseAuth.getInstance().getCurrentUser().getUid();

                } else {
                    onSignedOut();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER)

                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };


        childEventlistener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ArrayList<Faculty> time;

                time = new ArrayList<>();
                for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    for (DataSnapshot mySnapShot : snapShot.getChildren()) {
                        String string = mySnapShot.getValue(String.class);
                        time.add(new Faculty(string,dataSnapshot.getKey()));
                    }
                }
                finalTimelist=new ArrayList<>();
                ArrayList<String> splittedTime=new ArrayList<>();;
                for(Faculty f: time){
                    if(((int)f.getName().charAt(0)>=48)&&((int)f.getName().charAt(0)<=57)){
                        splittedTime.add(f.getName());
                    }
                    else{
                        finalTimelist.add(new FacultyTime(splittedTime,f.getName(),f.getId()));
                        splittedTime=new ArrayList<>();
                    }
                }

                for(FacultyTime f:finalTimelist) {
                    if (userId.equals(f.getUserId())) {
                        for(String str:f.getTime()) {
                            myFreeTimeList.add(str);
                            arrayAdapter.notifyDataSetChanged();
                        }

                    }
                }

            }

                @Override
                public void onChildChanged (DataSnapshot dataSnapshot, String s){

                }

                @Override
                public void onChildRemoved (DataSnapshot dataSnapshot){

                }

                @Override
                public void onChildMoved (DataSnapshot dataSnapshot, String s){

                }

                @Override
                public void onCancelled (DatabaseError databaseError){

                }

        };



    }





    public void saveTime(View view){

        String time=leftTime.getText()+" - "+ rightTIme.getText();
            myFreeTimeList.add(time);
            arrayAdapter.notifyDataSetChanged();
            facultyDatabaseReference.child(userId).child("timeList").push().setValue(time);


    }

    protected void onResume(){
        super.onResume();
        facultyAuth.addAuthStateListener(authStateListener);
        facultyDatabaseReference.addChildEventListener(childEventlistener);


    }
    protected void onPause(){
        super.onPause();
        facultyAuth.removeAuthStateListener(authStateListener);
        facultyDatabaseReference.removeEventListener(childEventlistener);
        myFreeTimeList.clear();
        arrayAdapter.notifyDataSetChanged();


    }

    private void onSignedIn(){
        username=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference usernameReference=facultyDatabaseReference.child(userId).child("username");


        usernameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()== false){
                    Toast.makeText(FacultyActivity.this, username, Toast.LENGTH_SHORT).show();
                    usernameReference.push().setValue(username);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private void onSignedOut(){
        myFreeTimeList.clear();
        arrayAdapter.clear();
        arrayAdapter.notifyDataSetChanged();

    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==RC_SIGN_IN){
            if(resultCode==RESULT_OK) {
                Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
                username=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                final DatabaseReference usernameReference=facultyDatabaseReference.child(userId).child("username");


                usernameReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()== false){
                            Toast.makeText(FacultyActivity.this, username, Toast.LENGTH_SHORT).show();
                            usernameReference.push().setValue(username);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {


                    }
                });

            }
            else if(resultCode==RESULT_CANCELED){
                Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }


            }
        }

    }
