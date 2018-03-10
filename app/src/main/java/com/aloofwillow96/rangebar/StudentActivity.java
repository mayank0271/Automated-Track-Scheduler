package com.aloofwillow96.rangebar;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.ArrayList;


public class StudentActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    ListView facultyListView;
    ArrayList<String> facultyList;
    final static int RC_SIGN_IN = 1;
    ArrayAdapter<String> arrayAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference facultyDatabaseReference,studentDatabaseReference;
    ChildEventListener childEventlistener;
    ArrayList<FacultyTime>finalTimelist;
    static ArrayList<FacultyTime>list=new ArrayList<>();
    String username;
    String userId;
    FirebaseUser user;
    static String loggedInUserEmail;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.studentmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu: AuthUI.getInstance().signOut(this);
                break;

        }
        return super.onOptionsItemSelected(item);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        firebaseAuth = FirebaseAuth.getInstance();

        facultyListView=(ListView)findViewById(R.id.listView);
        firebaseDatabase=FirebaseDatabase.getInstance();
        facultyDatabaseReference=firebaseDatabase.getReference().child("faculty");
        studentDatabaseReference=firebaseDatabase.getReference().child("student");


        facultyList=new ArrayList<>();
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,facultyList);
        facultyListView.setAdapter(arrayAdapter);
        finalTimelist=new ArrayList<>();

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
              //  .setNotificationReceivedHandler(new ExampleNotificationRecievedHandler())
                //.setNotificationOpenedHandler(new StudentFacultyInteractActivity.ExampleNotificationOpenedHandler())
                .init();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Toast.makeText(StudentActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
                    userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Toast.makeText(StudentActivity.this, userId, Toast.LENGTH_SHORT).show();
                    //onSignedIn();
                    loggedInUserEmail=firebaseUser.getEmail();
                    OneSignal.sendTag("User_ID",loggedInUserEmail);


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
                        //Toast.makeText(StudentActivity.this, string, Toast.LENGTH_SHORT).show();
                        time.add(new Faculty(string,dataSnapshot.getKey()));
                    }
                }
                finalTimelist.clear();

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

                    facultyList.add(f.getUsername());
                    list.add(new FacultyTime(f.getTime(),f.getUsername(),f.getUserId()));
                   // Toast.makeText(StudentActivity.this, f.getTime().toString(), Toast.LENGTH_SHORT).show();
                    arrayAdapter.notifyDataSetChanged();
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
        facultyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getApplicationContext(),StudentFacultyInteractActivity.class);
                Toast.makeText(StudentActivity.this, list.get(i).getUserId(), Toast.LENGTH_SHORT).show();
                intent.putExtra("key",list.get(i).getUserId());
                intent.putExtra("sId",FirebaseAuth.getInstance().getCurrentUser().getEmail());

                startActivity(intent);
            }
        });



    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
                username=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                final DatabaseReference usernameReference=studentDatabaseReference.child(userId).child("username");


                usernameReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()== false){
                           // Toast.makeText(StudentActivity.this, username, Toast.LENGTH_SHORT).show();
                            usernameReference.push().setValue(username);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {


                    }
                });

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }


        }
    }

        private void onSignedIn(){
            username=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
            final DatabaseReference usernameReference=studentDatabaseReference.child(userId).child("username");


            usernameReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()== false){
                        Toast.makeText(StudentActivity.this, username, Toast.LENGTH_SHORT).show();
                        usernameReference.push().setValue(username);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    void onSignedOut(){
        facultyList.clear();
        list.clear();
        arrayAdapter.clear();
        arrayAdapter.notifyDataSetChanged();
    }
    protected void onResume(){
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
        facultyDatabaseReference.addChildEventListener(childEventlistener);

    }
    protected void onPause(){
        super.onPause();
        facultyList.clear();
        arrayAdapter.notifyDataSetChanged();
        firebaseAuth.removeAuthStateListener(authStateListener);
        facultyDatabaseReference.removeEventListener(childEventlistener);


    }


}
