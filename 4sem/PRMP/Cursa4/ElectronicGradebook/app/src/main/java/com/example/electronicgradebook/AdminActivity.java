package com.example.electronicgradebook;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private static final String TAG = "AdminActivity";
    private RecyclerView recyclerViewGroups;
    private GroupAdapter groupAdapter;
    private List<Group> groups;
    private FloatingActionButton fabAddGroup;
    private FloatingActionButton fabAddStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Инициализация UI компонентов
        recyclerViewGroups = findViewById(R.id.recyclerViewGroups);
        fabAddGroup = findViewById(R.id.fabAddGroup);
        fabAddStudent = findViewById(R.id.fabAddStudent);

        // Настройка RecyclerView
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(this));
        groups = new ArrayList<>(MockData.getGroups().values());
        groupAdapter = new GroupAdapter(groups);
        recyclerViewGroups.setAdapter(groupAdapter);

        // Обработчики нажатий
        fabAddGroup.setOnClickListener(v -> showAddGroupDialog());
        fabAddStudent.setOnClickListener(v -> showAddStudentDialog());

        Log.d(TAG, "AdminActivity created with " + groups.size() + " groups");
    }

    private void showAddGroupDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_group, null);
        EditText editGroupName = dialogView.findViewById(R.id.editGroupName);

        new AlertDialog.Builder(this)
            .setTitle("Добавить группу")
            .setView(dialogView)
            .setPositiveButton("Добавить", (dialog, which) -> {
                String groupName = editGroupName.getText().toString().trim();
                if (!groupName.isEmpty()) {
                    addNewGroup(groupName);
                } else {
                    Toast.makeText(this, "Введите название группы", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Отмена", null)
            .show();
    }

    private void showAddStudentDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_student, null);
        EditText editLastName = dialogView.findViewById(R.id.editLastName);
        EditText editFirstName = dialogView.findViewById(R.id.editFirstName);
        Spinner spinnerGroup = dialogView.findViewById(R.id.spinnerGroup);

        // Настройка спинера групп
        List<Group> groupsList = new ArrayList<>(MockData.getGroups().values());
        ArrayAdapter<Group> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, groupsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroup.setAdapter(adapter);

        new AlertDialog.Builder(this)
            .setTitle("Добавить студента")
            .setView(dialogView)
            .setPositiveButton("Добавить", (dialog, which) -> {
                String lastName = editLastName.getText().toString().trim();
                String firstName = editFirstName.getText().toString().trim();
                Group selectedGroup = (Group) spinnerGroup.getSelectedItem();

                if (!lastName.isEmpty() && !firstName.isEmpty() && selectedGroup != null) {
                    addNewStudent(lastName, firstName, selectedGroup.getId());
                } else {
                    Toast.makeText(this, "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Отмена", null)
            .show();
    }

    private void addNewGroup(String groupName) {
        try {
            String groupId = "group" + (groups.size() + 1);
            Group newGroup = new Group(groupId, groupName);
            MockData.addGroup(newGroup);
            groups.add(newGroup);
            groupAdapter.notifyItemInserted(groups.size() - 1);
            Toast.makeText(this, "Группа добавлена", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Added new group: " + groupName);
        } catch (Exception e) {
            Log.e(TAG, "Error adding group", e);
            Toast.makeText(this, "Ошибка при добавлении группы", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewStudent(String lastName, String firstName, String groupId) {
        try {
            String studentId = "student" + System.currentTimeMillis();
            Student newStudent = new Student(studentId, lastName, firstName, groupId);
            MockData.addStudent(newStudent);
            Toast.makeText(this, "Студент добавлен", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Added new student: " + lastName + " " + firstName);
        } catch (Exception e) {
            Log.e(TAG, "Error adding student", e);
            Toast.makeText(this, "Ошибка при добавлении студента", Toast.LENGTH_SHORT).show();
        }
    }
} 