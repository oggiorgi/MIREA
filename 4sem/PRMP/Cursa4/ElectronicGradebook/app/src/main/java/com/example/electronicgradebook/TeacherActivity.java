package com.example.electronicgradebook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TeacherActivity extends AppCompatActivity {
    private Button btnMarkStudents, btnViewGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        // Initialize buttons
        btnMarkStudents = findViewById(R.id.btnMarkStudents);
        btnViewGroups = findViewById(R.id.btnViewGroups);

        // Set click listeners
        btnMarkStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, MarkStudentsActivity.class);
                startActivity(intent);
            }
        });

        btnViewGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, ViewGroupsActivity.class);
                startActivity(intent);
            }
        });
    }
} 