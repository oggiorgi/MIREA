package com.example.electronicgradebook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewGroupsActivity extends AppCompatActivity {
    private static final String TAG = "ViewGroupsActivity";
    private RecyclerView recyclerView;
    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_groups);

        recyclerView = findViewById(R.id.recyclerViewGroups);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Загружаем список групп
        loadGroups();
    }

    private void loadGroups() {
        Map<String, Group> groupsMap = MockData.getGroups();
        List<Group> groups = new ArrayList<>(groupsMap.values());
        Log.d(TAG, "Loaded " + groups.size() + " groups");

        // Добавляем логирование для каждой группы
        for (Group group : groups) {
            List<Student> students = MockData.getStudentsByGroup(group.getId());
            Log.d(TAG, "Group " + group.getName() + " has " + students.size() + " students");
            for (Student student : students) {
                Log.d(TAG, "Student in group: " + student.getFullName() + " (ID: " + student.getId() + ")");
            }
        }

        adapter = new GroupAdapter(groups);
        recyclerView.setAdapter(adapter);
    }

    private class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
        private List<Group> groups;

        public GroupAdapter(List<Group> groups) {
            this.groups = groups;
        }

        @Override
        public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
            return new GroupViewHolder(view);
        }

        @Override
        public void onBindViewHolder(GroupViewHolder holder, int position) {
            Group group = groups.get(position);
            holder.bind(group);
        }

        @Override
        public int getItemCount() {
            return groups.size();
        }

        class GroupViewHolder extends RecyclerView.ViewHolder {
            private TextView textGroupName;
            private TextView textStudentCount;
            private View studentsContainer;
            private boolean isExpanded = false;

            public GroupViewHolder(View itemView) {
                super(itemView);
                textGroupName = itemView.findViewById(R.id.textGroupName);
                textStudentCount = itemView.findViewById(R.id.textStudentCount);
                studentsContainer = itemView.findViewById(R.id.studentsContainer);

                // Обработчик нажатия на карточку группы
                itemView.setOnClickListener(v -> {
                    isExpanded = !isExpanded;
                    studentsContainer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                });
            }

            public void bind(Group group) {
                // Создаем контейнер для имени группы и кнопки удаления
                LinearLayout headerRow = new LinearLayout(itemView.getContext());
                headerRow.setOrientation(LinearLayout.HORIZONTAL);
                headerRow.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                headerRow.setPadding(16, 16, 16, 8);

                // Создаем новый TextView для названия группы
                TextView groupNameView = new TextView(itemView.getContext());
                groupNameView.setText(group.getName());
                groupNameView.setTextSize(18);
                groupNameView.setTypeface(null, android.graphics.Typeface.BOLD);
                groupNameView.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.black));
                groupNameView.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
                ));
                headerRow.addView(groupNameView);

                // Кнопка удаления группы
                ImageButton deleteGroupButton = new ImageButton(itemView.getContext());
                deleteGroupButton.setImageResource(android.R.drawable.ic_menu_delete);
                deleteGroupButton.setBackgroundResource(android.R.color.transparent);
                deleteGroupButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                deleteGroupButton.setOnClickListener(v -> {
                    new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Удаление группы")
                        .setMessage("Вы уверены, что хотите удалить группу " + group.getName() + "?\nВсе студенты группы также будут удалены.")
                        .setPositiveButton("Да", (dialog, which) -> {
                            MockData.deleteGroup(group.getId());
                            // Обновляем список групп
                            loadGroups();
                        })
                        .setNegativeButton("Нет", null)
                        .show();
                });
                headerRow.addView(deleteGroupButton);

                // Заменяем старый textGroupName на новый headerRow
                ViewGroup parent = (ViewGroup) textGroupName.getParent();
                if (parent != null) {
                    int index = parent.indexOfChild(textGroupName);
                    parent.removeView(textGroupName);
                    parent.addView(headerRow, index);
                }
                
                // Получаем список студентов группы
                List<Student> students = MockData.getStudentsByGroup(group.getId());
                textStudentCount.setText(students.size() + " студентов");

                // Очищаем контейнер студентов
                if (studentsContainer instanceof ViewGroup) {
                    ((ViewGroup) studentsContainer).removeAllViews();
                }

                // Добавляем студентов в контейнер
                for (Student student : students) {
                    LinearLayout studentRow = new LinearLayout(itemView.getContext());
                    studentRow.setOrientation(LinearLayout.HORIZONTAL);
                    studentRow.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    studentRow.setPadding(16, 8, 16, 8);

                    // Имя студента
                    TextView studentView = new TextView(itemView.getContext());
                    studentView.setText(student.getFullName());
                    studentView.setTextSize(14);
                    studentView.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                    ));

                    // Кнопка удаления студента
                    ImageButton deleteButton = new ImageButton(itemView.getContext());
                    deleteButton.setImageResource(android.R.drawable.ic_menu_delete);
                    deleteButton.setBackgroundResource(android.R.color.transparent);
                    deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    deleteButton.setOnClickListener(v -> {
                        new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Удаление студента")
                            .setMessage("Вы уверены, что хотите удалить " + student.getFullName() + "?")
                            .setPositiveButton("Да", (dialog, which) -> {
                                MockData.deleteStudent(student.getId());
                                // Обновляем список студентов
                                bind(group);
                            })
                            .setNegativeButton("Нет", null)
                            .show();
                    });

                    studentRow.addView(studentView);
                    studentRow.addView(deleteButton);
                    ((ViewGroup) studentsContainer).addView(studentRow);
                }

                // Скрываем список студентов при переиспользовании ViewHolder
                studentsContainer.setVisibility(View.GONE);
                isExpanded = false;
            }
        }
    }
} 