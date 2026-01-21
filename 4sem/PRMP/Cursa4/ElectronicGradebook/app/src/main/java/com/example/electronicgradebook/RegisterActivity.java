package com.example.electronicgradebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.AdapterView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText editFirstName, editLastName, editEmail, editPassword;
    private Spinner spinnerRole, spinnerGroup;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<String> groupList = new ArrayList<>();
    private ArrayAdapter<String> groupAdapter;
    private TextView groupLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        spinnerGroup = findViewById(R.id.spinnerGroup);
        btnRegister = findViewById(R.id.btnRegister);
        groupLabel = findViewById(R.id.groupLabel);

        // Настройка спиннера ролей
        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        // Настройка спиннера групп
        groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupList);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroup.setAdapter(groupAdapter);
        loadGroupsFromFirestore();

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String role = spinnerRole.getSelectedItem().toString();
                if (role.equals("Ученик")) {
                    groupLabel.setVisibility(View.VISIBLE);
                    spinnerGroup.setVisibility(View.VISIBLE);
                } else {
                    groupLabel.setVisibility(View.GONE);
                    spinnerGroup.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnRegister.setOnClickListener(v -> {
            String firstName = editFirstName.getText().toString().trim();
            String lastName = editLastName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            String role = spinnerRole.getSelectedItem().toString();
            String group = spinnerGroup.getSelectedItem() != null ? spinnerGroup.getSelectedItem().toString() : "";
            if (role.equals("Учитель")) role = "teacher";
            else if (role.equals("Ученик")) role = "student";
            else if (role.equals("Администратор")) role = "admin";
            final String finalRole = role;

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || group.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все поля и выберите группу", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("firstName", firstName);
                            userMap.put("lastName", lastName);
                            userMap.put("email", email);
                            userMap.put("role", finalRole);
                            userMap.put("group", group);
                            userMap.put("id", userId);
                            db.collection("users").document(userId).set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Ошибка сохранения данных: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        } else {
                            Toast.makeText(this, "Ошибка регистрации: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private void loadGroupsFromFirestore() {
        db.collection("groups").get().addOnSuccessListener(queryDocumentSnapshots -> {
            groupList.clear();
            if (queryDocumentSnapshots.isEmpty()) {
                // Добавляем стандартные группы, если их нет
                addDefaultGroups();
            } else {
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    String groupName = doc.getString("name");
                    if (groupName != null) groupList.add(groupName);
                }
                groupAdapter.notifyDataSetChanged();
                if (groupList.isEmpty()) {
                    Toast.makeText(this, "Нет доступных групп. Обратитесь к администратору.", Toast.LENGTH_LONG).show();
                    btnRegister.setEnabled(false);
                } else {
                    btnRegister.setEnabled(true);
                }
            }
        }).addOnFailureListener(e ->
            Toast.makeText(this, "Ошибка загрузки групп: " + e.getMessage(), Toast.LENGTH_LONG).show()
        );
    }

    private void addDefaultGroups() {
        Map<String, Object> groupA = new HashMap<>();
        groupA.put("name", "11А");
        Map<String, Object> groupB = new HashMap<>();
        groupB.put("name", "11Б");
        Map<String, Object> groupV = new HashMap<>();
        groupV.put("name", "11В");
        db.collection("groups").document("group1").set(groupA);
        db.collection("groups").document("group2").set(groupB);
        db.collection("groups").document("group3").set(groupV)
            .addOnSuccessListener(aVoid -> loadGroupsFromFirestore());
    }
} 