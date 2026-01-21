package com.example.electronicgradebook;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import com.example.electronicgradebook.Group;
import com.example.electronicgradebook.Student;

public class MarkStudentsActivity extends AppCompatActivity {
    private static final String TAG = "MarkStudentsActivity";
    private Spinner spinnerGroup;
    private Spinner spinnerSubject;
    private LinearLayout studentListContainer;
    private TextView textClassInfo;
    private TextView textMonthYear;
    private String selectedDate;
    private String selectedGroupId;
    private String selectedSubject;
    private List<Button> dateButtons = new ArrayList<>();
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat MONTH_YEAR_FORMAT = new SimpleDateFormat("LLLL yyyy", new Locale("ru"));
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("d", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_students);
        
        Log.d(TAG, "onCreate started");

        // Инициализация views
        spinnerGroup = findViewById(R.id.spinnerGroup);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        studentListContainer = findViewById(R.id.studentListContainer);
        textClassInfo = findViewById(R.id.textClassInfo);
        textMonthYear = findViewById(R.id.textMonthYear);

        // Загружаем список групп
        loadGroups();

        // Устанавливаем текущий месяц и год
        textMonthYear.setText(MONTH_YEAR_FORMAT.format(new Date()));

        // Генерируем кнопки с датами
        generateDateButtons();

        // Обработчик выбора группы
        spinnerGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Group selectedGroup = (Group) parent.getItemAtPosition(position);
                selectedGroupId = selectedGroup.getId();
                Log.d(TAG, "Selected group: " + selectedGroup.getName() + " (ID: " + selectedGroupId + ")");
                updateSubjects();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGroupId = null;
                spinnerSubject.setAdapter(null);
            }
        });

        // Обработчик выбора предмета
        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSubject = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "Selected subject: " + selectedSubject);
                if (selectedDate != null) {
                    loadStudents();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSubject = null;
                studentListContainer.removeAllViews();
            }
        });
    }

    private void generateDateButtons() {
        LinearLayout datesContainer = findViewById(R.id.datesContainer);
        datesContainer.removeAllViews();
        dateButtons.clear();

        // Создаем кнопки для каждого дня сентября 2024
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.SEPTEMBER, 1);

        // Создаем контейнер для горизонтальной прокрутки
        HorizontalScrollView scrollView = new HorizontalScrollView(this);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        scrollView.setLayoutParams(scrollParams);

        // Создаем контейнер для кнопок
        LinearLayout buttonsContainer = new LinearLayout(this);
        buttonsContainer.setOrientation(LinearLayout.HORIZONTAL);
        buttonsContainer.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Добавляем кнопки для каждого дня
        while (calendar.get(Calendar.MONTH) == Calendar.SEPTEMBER) {
            // Пропускаем выходные
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                Button dateButton = new Button(this);
                dateButton.setText(DAY_FORMAT.format(calendar.getTime()));
                
                // Сохраняем дату в миллисекундах в теге кнопки
                dateButton.setTag(calendar.getTimeInMillis());
                
                // Устанавливаем обработчик нажатия
                dateButton.setOnClickListener(v -> {
                    // Получаем дату из тега кнопки
                    long dateInMillis = (long) v.getTag();
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(dateInMillis);
                    selectedDate = DATE_FORMAT.format(cal.getTime());
                    
                    // Обновляем UI
                    for (Button btn : dateButtons) {
                        btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.purple_500));
                    }
                    dateButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.purple_700));
                    
                    // Загружаем список студентов
                    if (selectedSubject != null) {
                        loadStudents();
                    }
                });
                
                // Добавляем кнопку в контейнер
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                );
                buttonParams.setMargins(8, 0, 8, 0);
                dateButton.setLayoutParams(buttonParams);
                buttonsContainer.addView(dateButton);
                dateButtons.add(dateButton);
            }
            
            // Переходим к следующему дню
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Добавляем контейнер с кнопками в ScrollView
        scrollView.addView(buttonsContainer);
        datesContainer.addView(scrollView);

        // Выбираем первую кнопку по умолчанию
        if (!dateButtons.isEmpty()) {
            dateButtons.get(0).performClick();
        }
    }

    private void loadGroups() {
        Map<String, Group> groupsMap = MockData.getGroups();
        List<Group> uniqueGroups = new ArrayList<>();
        Set<String> seenNames = new HashSet<>();
        for (Group group : groupsMap.values()) {
            String name = group.getName().trim().toLowerCase();
            if (!seenNames.contains(name)) {
                uniqueGroups.add(group);
                seenNames.add(name);
            }
        }
        ArrayAdapter<Group> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, uniqueGroups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroup.setAdapter(adapter);
    }

    private void updateSubjects() {
        if (selectedGroupId == null) {
            spinnerSubject.setAdapter(null);
            return;
        }

        try {
            // Получаем день недели для выбранной даты
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DATE_FORMAT.parse(selectedDate));
            
            // Получаем день недели на русском языке
            String[] daysOfWeek = {"Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
            String dayOfWeek = daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1];
            Log.d(TAG, "Day of week: " + dayOfWeek);
            
            // Получаем предметы на выбранный день
            List<String> subjects = MockData.getSubjectsForGroupAndDay(selectedGroupId, dayOfWeek);
            Log.d(TAG, "Found " + subjects.size() + " subjects for group " + selectedGroupId + " on " + dayOfWeek);
            
            ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, subjects);
            subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSubject.setAdapter(subjectAdapter);
            
            // Обновляем информацию о классе
            Group selectedGroup = null;
            for (Group group : MockData.getGroups().values()) {
                if (group.getId().equals(selectedGroupId)) {
                    selectedGroup = group;
                    break;
                }
            }
            if (selectedGroup != null) {
                textClassInfo.setText(selectedGroup.getName());
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error updating subjects", e);
            Toast.makeText(this, "Ошибка при загрузке предметов", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadStudents() {
        if (selectedGroupId == null || selectedSubject == null || selectedDate == null) {
            Log.d(TAG, "Cannot load students: missing required data");
            return;
        }

        Log.d(TAG, "Loading students for group: " + selectedGroupId + ", subject: " + selectedSubject + ", date: " + selectedDate);
        studentListContainer.removeAllViews();

        try {
            // Получаем список студентов группы
            String normGroup = MockData.normalizeGroupName(selectedGroupId);
            List<Student> students = MockData.getStudentsByGroup(normGroup);
            Log.d(TAG, "Found " + students.size() + " students in group");
            
            if (students.isEmpty()) {
                TextView noStudentsText = new TextView(this);
                noStudentsText.setText("В группе нет студентов");
                noStudentsText.setTextSize(16);
                noStudentsText.setPadding(16, 16, 16, 16);
                studentListContainer.addView(noStudentsText);
                return;
            }
            
            for (Student student : students) {
                View studentView = getLayoutInflater().inflate(R.layout.item_student_attendance, studentListContainer, false);
                TextView studentNameText = studentView.findViewById(R.id.textStudentName);
                RadioGroup attendanceGroup = studentView.findViewById(R.id.radioGroupAttendance);
                
                if (studentNameText == null || attendanceGroup == null) {
                    Log.e(TAG, "Could not find views in student attendance layout");
                    continue;
                }
                
                studentNameText.setText(student.getFullName());
                
                // Получаем сохраненную отметку
                String savedMark = MockData.getAttendance(selectedGroupId, selectedDate, selectedSubject, student.getId());
                Log.d(TAG, "Student " + student.getFullName() + " attendance: " + savedMark);
                
                // Устанавливаем сохраненную отметку, если она есть
                if (savedMark != null) {
                    switch (savedMark) {
                        case "present":
                            attendanceGroup.check(R.id.radioPresent);
                            break;
                        case "absent":
                            attendanceGroup.check(R.id.radioAbsent);
                            break;
                        case "excused":
                            attendanceGroup.check(R.id.radioExcused);
                            break;
                    }
                }
                
                // Добавляем обработчик изменения отметки
                attendanceGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    String mark;
                    if (checkedId == R.id.radioPresent) {
                        mark = "present";
                    } else if (checkedId == R.id.radioAbsent) {
                        mark = "absent";
                    } else if (checkedId == R.id.radioExcused) {
                        mark = "excused";
                    } else {
                        return;
                    }
                    
                    // Сохраняем отметку
                    MockData.saveAttendance(selectedGroupId, selectedDate, selectedSubject, student.getId(), mark);
                    Log.d(TAG, "Saved attendance for " + student.getFullName() + ": " + mark);
                });
                
                studentListContainer.addView(studentView);
                Log.d(TAG, "Added student to container: " + student.getFullName());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading students", e);
            Toast.makeText(this, "Ошибка при загрузке списка студентов", Toast.LENGTH_SHORT).show();
        }
    }
} 