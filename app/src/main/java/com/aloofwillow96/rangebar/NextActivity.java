package com.aloofwillow96.rangebar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        Button student = (Button)findViewById(R.id.student);
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new Intent(NextActivity.this,StudentActivity.class);
                startActivity(newIntent);
            }
        });

        Button faculty = (Button)findViewById(R.id.faculty);
        faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new Intent(NextActivity.this,FacultyActivity.class);
                startActivity(newIntent);
            }
        });
    }
}
