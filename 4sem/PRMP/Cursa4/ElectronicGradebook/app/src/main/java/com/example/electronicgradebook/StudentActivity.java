package com.example.electronicgradebook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.electronicgradebook.R;
import com.example.electronicgradebook.Group;
import com.example.electronicgradebook.Student;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class StudentActivity extends AppCompatActivity {
    private static final String TAG = "StudentActivity";
    private LinearLayout subjectsContainer;
    private LinearLayout datesContainer;
    private TextView textClassInfo;
    private TextView textMonthYear;
    private String studentId;
    private String groupId;
    private String selectedDate;
    private List<Button> dateButtons = new ArrayList<>();
    private String selectedSubject;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat MONTH_YEAR_FORMAT = new SimpleDateFormat("LLLL yyyy", new Locale("ru"));
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("d", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        
        Log.d(TAG, "onCreate started");

        // Получаем ID студента из Intent
        studentId = getIntent().getStringExtra("STUDENT_ID");
        Log.d(TAG, "Student ID from intent: " + studentId);
        
        if (studentId == null) {
            Log.e(TAG, "No student ID provided");
            Toast.makeText(this, "Ошибка: не указан ID студента", Toast.LENGTH_SHORT).show();
            StudentActivity.this.finish();
            return;
        }

        // Инициализация views
        subjectsContainer = findViewById(R.id.subjectsContainer);
        datesContainer = findViewById(R.id.datesContainer);
        textClassInfo = findViewById(R.id.textClassInfo);
        textMonthYear = findViewById(R.id.textMonthYear);

        if (subjectsContainer == null || datesContainer == null || textClassInfo == null || textMonthYear == null) {
            Log.e(TAG, "Could not find required views");
            return;
        }

        // Загружаем профиль студента из Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(studentId).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (!documentSnapshot.exists()) {
                    Log.e(TAG, "Профиль студента не найден");
                    Toast.makeText(this, "Профиль студента не найден", Toast.LENGTH_SHORT).show();
                    StudentActivity.this.finish();
                    return;
                }
                String group = documentSnapshot.getString("group");
                if (group == null || group.isEmpty()) {
                    Log.e(TAG, "Группа не указана для студента");
                    Toast.makeText(this, "Группа не указана для студента", Toast.LENGTH_SHORT).show();
                    StudentActivity.this.finish();
                    return;
                }
                groupId = group;
                Log.d(TAG, "Loaded group for student: " + groupId);
                
                // Устанавливаем информацию о классе
                textClassInfo.setText(groupId);
                textMonthYear.setText(MONTH_YEAR_FORMAT.format(new Date()));
                
                // Генерируем кнопки с датами
                generateDateButtons();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Ошибка загрузки профиля: " + e.getMessage());
                Toast.makeText(this, "Ошибка загрузки профиля: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                StudentActivity.this.finish();
            });
    }

    private void generateDateButtons() {
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
                    
                    // Загружаем расписание
                    loadSchedule(selectedDate);
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

    private void loadSchedule(String date) {
        Log.d(TAG, "Loading schedule for date: " + date + ", group: " + groupId);
        subjectsContainer.removeAllViews();
        selectedSubject = null;

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DATE_FORMAT.parse(date));

            String[] daysOfWeek = {"Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
            String dayOfWeek = daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1];
            Log.d(TAG, "Day of week: " + dayOfWeek);

            List<String> subjects = MockData.getSubjectsForGroupAndDay(groupId, dayOfWeek);
            if (!subjects.isEmpty()) {
                showSubjects(subjects, date);
            } else {
                // Если в MockData нет предметов — пробуем загрузить из Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("schedule")
                    .whereEqualTo("groupId", groupId)
                    .whereEqualTo("dayOfWeek", dayOfWeek)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<String> firebaseSubjects = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String subject = doc.getString("subject");
                            if (subject != null && !subject.isEmpty()) {
                                firebaseSubjects.add(subject);
                            }
                        }
                        showSubjects(firebaseSubjects, date);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Ошибка загрузки расписания из Firebase", Toast.LENGTH_SHORT).show();
                        showSubjects(new ArrayList<>(), date);
                    });
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка при загрузке расписания", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSubjects(List<String> subjects, String date) {
        if (subjects.isEmpty()) {
            TextView noSubjectsText = new TextView(this);
            noSubjectsText.setText("В этот день нет занятий");
            noSubjectsText.setTextSize(16);
            noSubjectsText.setPadding(16, 16, 16, 16);
            subjectsContainer.addView(noSubjectsText);
            return;
        }
        for (String subject : subjects) {
            View subjectView = getLayoutInflater().inflate(R.layout.item_subject_schedule, subjectsContainer, false);
            TextView subjectNameText = subjectView.findViewById(R.id.textSubjectName);
            if (subjectNameText == null) continue;
            subjectNameText.setText(subject);
            subjectView.setOnClickListener(v -> {
                selectedSubject = subject;
                showStudentAttendance(subject, date);
            });
            subjectsContainer.addView(subjectView);
        }
    }

    private void showStudentAttendance(String subject, String date) {
        subjectsContainer.removeAllViews();
        TextView subjectHeader = new TextView(this);
        subjectHeader.setText(subject);
        subjectHeader.setTextSize(20);
        subjectHeader.setTextColor(ContextCompat.getColor(this, R.color.black));
        subjectHeader.setPadding(16, 16, 16, 24);
        subjectsContainer.addView(subjectHeader);

        // Получаем список студентов группы
        List<Student> students = MockData.getStudentsByGroup(groupId);
        Log.d(TAG, "Showing attendance for " + students.size() + " students in group " + groupId);
        
        for (Student student : students) {
            View studentView = getLayoutInflater().inflate(R.layout.item_student_attendance, subjectsContainer, false);
            TextView studentNameText = studentView.findViewById(R.id.textStudentName);
            RadioGroup attendanceGroup = studentView.findViewById(R.id.radioGroupAttendance);
            
            if (studentNameText == null || attendanceGroup == null) {
                Log.e(TAG, "Could not find views in student attendance layout");
                continue;
            }
            
            studentNameText.setText(student.getFullName());
            
            // Получаем сохраненную отметку
            String savedMark = MockData.getAttendance(groupId, date, subject, student.getId());
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
            
            // Отключаем возможность изменения отметок для студентов
            RadioButton radioPresent = studentView.findViewById(R.id.radioPresent);
            RadioButton radioAbsent = studentView.findViewById(R.id.radioAbsent);
            RadioButton radioExcused = studentView.findViewById(R.id.radioExcused);
            if (radioPresent != null) radioPresent.setEnabled(false);
            if (radioAbsent != null) radioAbsent.setEnabled(false);
            if (radioExcused != null) radioExcused.setEnabled(false);
            
            subjectsContainer.addView(studentView);
            Log.d(TAG, "Added student view for: " + student.getFullName());
        }
        
        Button backButton = new Button(this);
        backButton.setText("Назад к расписанию");
        backButton.setOnClickListener(v -> loadSchedule(date));
        subjectsContainer.addView(backButton);
    }
} 