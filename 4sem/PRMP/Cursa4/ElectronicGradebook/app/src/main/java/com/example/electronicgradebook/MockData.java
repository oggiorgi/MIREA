package com.example.electronicgradebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.example.electronicgradebook.Group;
import com.example.electronicgradebook.Student;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class MockData {
    private static final String TAG = "MockData";
    private static final String[] DAYS_OF_WEEK = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
    private static Context appContext;
    private static boolean isInitialized = false;
    private static Map<String, Group> groups = new HashMap<>();
    private static Map<String, Student> students = new HashMap<>();
    private static Map<String, Map<String, List<String>>> schedule = new HashMap<>();
    private static Map<String, String> attendanceMarks = new HashMap<>();
    private static final String PREFS_NAME = "ElectronicGradebookPrefs";
    private static final String KEY_STUDENTS = "students";
    private static final String KEY_GROUPS = "groups";

    public static void init(Context context) {
        if (isInitialized) {
            Log.d(TAG, "MockData already initialized");
            return;
        }
        Log.d(TAG, "Initializing MockData");
        appContext = context.getApplicationContext();
        loadSavedData();
        // Если нет сохранённых данных, инициализируем начальные
        if (groups.isEmpty()) {
            Log.d(TAG, "No saved groups found, initializing default groups");
            addDefaultGroup("11А");
            addDefaultGroup("11Б");
            addDefaultGroup("11В");
            saveGroups();
        } else {
            Log.d(TAG, "Loaded " + groups.size() + " groups from storage");
        }
        if (students.isEmpty()) {
            Log.d(TAG, "No saved students found, initializing default students");
            students.put("student1", new Student("student1", "Иванов", "Иван", "11А"));
            students.put("student2", new Student("student2", "Петров", "Петр", "11А"));
            students.put("student3", new Student("student3", "Сидоров", "Сидор", "11А"));
            students.put("student4", new Student("student4", "Смирнова", "Анна", "11Б"));
            students.put("student5", new Student("student5", "Козлова", "Мария", "11Б"));
            students.put("student6", new Student("student6", "Новикова", "Елена", "11Б"));
            students.put("student7", new Student("student7", "Кузнецов", "Алексей", "11В"));
            students.put("student8", new Student("student8", "Морозов", "Дмитрий", "11В"));
            students.put("student9", new Student("student9", "Волков", "Сергей", "11В"));
            saveStudents();
        } else {
            Log.d(TAG, "Loaded " + students.size() + " students from storage");
        }
        // Инициализация расписания
        for (String groupName : groups.keySet()) {
            schedule.put(groupName, new HashMap<>());
            Map<String, List<String>> groupSchedule = schedule.get(groupName);
            groupSchedule.put("Понедельник", Arrays.asList("Математика", "Физика", "Информатика", "Русский язык", "Литература"));
            groupSchedule.put("Вторник", Arrays.asList("Английский язык", "История", "Обществознание", "Биология", "Химия"));
            groupSchedule.put("Среда", Arrays.asList("Математика", "Физика", "Информатика", "Русский язык", "Литература"));
            groupSchedule.put("Четверг", Arrays.asList("Английский язык", "История", "Обществознание", "Биология", "Химия"));
            groupSchedule.put("Пятница", Arrays.asList("Математика", "Физика", "Информатика", "Русский язык", "Литература"));
            groupSchedule.put("Суббота", Arrays.asList("Английский язык", "История", "Обществознание", "Биология", "Химия"));
        }
        loadAttendanceMarks();
        isInitialized = true;
        Log.d(TAG, "MockData initialized successfully");
    }

    private static void checkInitialization() {
        if (!isInitialized) {
            throw new IllegalStateException("MockData not initialized. Call MockData.init() first.");
        }
    }
    
    public static Map<String, Group> getGroups() {
        checkInitialization();
        Map<String, Group> normalizedGroups = new HashMap<>();
        for (Group group : groups.values()) {
            String normName = normalizeGroupName(group.getName());
            normalizedGroups.put(normName, new Group(normName, normName));
        }
        return normalizedGroups;
    }
    
    public static List<String> getSubjectsForGroupAndDay(String groupName, String dayOfWeek) {
        checkInitialization();
        Map<String, List<String>> groupSchedule = schedule.get(groupName);
        if (groupSchedule == null) {
            return new ArrayList<>();
        }
        List<String> subjects = groupSchedule.get(dayOfWeek);
        return subjects != null ? new ArrayList<>(subjects) : new ArrayList<>();
    }
    
    public static List<Student> getStudentsByGroup(String groupName) {
        checkInitialization();
        String normGroup = normalizeGroupName(groupName);
        List<Student> groupStudents = new ArrayList<>();
        for (Student student : students.values()) {
            if (normalizeGroupName(student.getGroupId()).equals(normGroup)) {
                groupStudents.add(student);
            }
        }
        return groupStudents;
    }

    private static void loadAttendanceMarks() {
        if (appContext == null) {
            Log.e(TAG, "Cannot load attendance marks: appContext is null");
            return;
        }
        
        SharedPreferences prefs = appContext.getSharedPreferences("attendance_marks", Context.MODE_PRIVATE);
        String marksString = prefs.getString("marks", "");
        Log.d(TAG, "Loading attendance marks: " + (marksString.isEmpty() ? "no saved marks" : marksString));
        
        attendanceMarks.clear();
        if (!marksString.isEmpty()) {
            String[] marks = marksString.split(";");
            for (String mark : marks) {
                if (mark.isEmpty()) continue;
                String[] parts = mark.split("=");
                if (parts.length == 2) {
                    attendanceMarks.put(parts[0], parts[1]);
                    Log.d(TAG, "Loaded mark: " + parts[0] + " -> " + parts[1]);
                }
            }
        }
        Log.d(TAG, "Loaded " + attendanceMarks.size() + " attendance marks");
    }

    public static void saveAttendance(String groupName, String date, String subject, String studentId, String status) {
        checkInitialization();
        String key = groupName + ":" + date + ":" + subject + ":" + studentId;
        Log.d(TAG, "Saving attendance: " + key + " -> " + status);
        attendanceMarks.put(key, status);
        saveAttendanceMarks();
    }

    public static String getAttendance(String groupName, String date, String subject, String studentId) {
        checkInitialization();
        String key = groupName + ":" + date + ":" + subject + ":" + studentId;
        String status = attendanceMarks.get(key);
        Log.d(TAG, "Getting attendance: " + key + " -> " + status);
        return status;
    }

    private static void saveAttendanceMarks() {
        if (appContext == null) {
            Log.e(TAG, "Cannot save attendance marks: appContext is null");
            return;
        }
        
        SharedPreferences prefs = appContext.getSharedPreferences("attendance_marks", Context.MODE_PRIVATE);
        StringBuilder sb = new StringBuilder();
        
        for (Map.Entry<String, String> entry : attendanceMarks.entrySet()) {
            if (sb.length() > 0) {
                sb.append(";");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        
        String marksString = sb.toString();
        Log.d(TAG, "Saving attendance marks: " + (marksString.isEmpty() ? "no marks to save" : marksString));
        
        prefs.edit().putString("marks", marksString).apply();
        Log.d(TAG, "Saved " + attendanceMarks.size() + " attendance marks");
    }

    public static void addGroup(Group group) {
        if (group != null && group.getName() != null) {
            String normName = normalizeGroupName(group.getName());
            groups.put(normName, new Group(normName, normName));
            schedule.put(normName, new HashMap<>());
            for (String day : DAYS_OF_WEEK) {
                schedule.get(normName).put(day, new ArrayList<>());
            }
            saveGroups();
            Log.d(TAG, "Added new group: " + normName);
        }
    }

    public static void addStudent(Student student) {
        if (student != null && student.getId() != null) {
            String normGroup = normalizeGroupName(student.getGroupId());
            students.put(student.getId(), new Student(student.getId(), student.getLastName(), student.getFirstName(), normGroup));
            saveStudents();
            Log.d(TAG, "Added new student: " + student.getLastName() + " " + student.getFirstName());
        }
    }

    public static Map<String, Student> getStudents() {
        return students;
    }

    private static void loadSavedData() {
        if (appContext == null) return;
        
        SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Загружаем группы
        String groupsJson = prefs.getString(KEY_GROUPS, "");
        if (!groupsJson.isEmpty()) {
            try {
                JSONObject json = new JSONObject(groupsJson);
                Iterator<String> keys = json.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject groupJson = json.getJSONObject(key);
                    groups.put(key, new Group(key, groupJson.getString("name")));
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error loading groups", e);
            }
        }
        
        // Загружаем студентов
        String studentsJson = prefs.getString(KEY_STUDENTS, "");
        if (!studentsJson.isEmpty()) {
            try {
                JSONObject json = new JSONObject(studentsJson);
                Iterator<String> keys = json.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject studentJson = json.getJSONObject(key);
                    students.put(key, new Student(
                        key,
                        studentJson.getString("lastName"),
                        studentJson.getString("firstName"),
                        studentJson.getString("groupId")
                    ));
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error loading students", e);
            }
        }
    }

    private static void saveGroups() {
        if (appContext == null) return;
        
        try {
            JSONObject json = new JSONObject();
            for (Map.Entry<String, Group> entry : groups.entrySet()) {
                JSONObject groupJson = new JSONObject();
                groupJson.put("name", entry.getValue().getName());
                json.put(entry.getKey(), groupJson);
            }
            
            SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_GROUPS, json.toString()).apply();
        } catch (JSONException e) {
            Log.e(TAG, "Error saving groups", e);
        }
    }

    private static void saveStudents() {
        if (appContext == null) return;
        
        try {
            JSONObject json = new JSONObject();
            for (Map.Entry<String, Student> entry : students.entrySet()) {
                JSONObject studentJson = new JSONObject();
                studentJson.put("lastName", entry.getValue().getLastName());
                studentJson.put("firstName", entry.getValue().getFirstName());
                studentJson.put("groupId", entry.getValue().getGroupId());
                json.put(entry.getKey(), studentJson);
            }
            
            SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_STUDENTS, json.toString()).apply();
        } catch (JSONException e) {
            Log.e(TAG, "Error saving students", e);
        }
    }

    public static void deleteStudent(String studentId) {
        if (students.containsKey(studentId)) {
            students.remove(studentId);
            saveStudents();
            Log.d(TAG, "Deleted student: " + studentId);
        }
    }

    public static void deleteGroup(String groupName) {
        if (groups.containsKey(groupName)) {
            groups.remove(groupName);
            schedule.remove(groupName);
            List<String> studentsToRemove = new ArrayList<>();
            for (Map.Entry<String, Student> entry : students.entrySet()) {
                if (entry.getValue().getGroupId().equals(groupName)) {
                    studentsToRemove.add(entry.getKey());
                }
            }
            for (String studentId : studentsToRemove) {
                students.remove(studentId);
            }
            List<String> marksToRemove = new ArrayList<>();
            for (String key : attendanceMarks.keySet()) {
                if (key.startsWith(groupName + ":")) {
                    marksToRemove.add(key);
                }
            }
            for (String key : marksToRemove) {
                attendanceMarks.remove(key);
            }
            saveGroups();
            saveStudents();
            saveAttendanceMarks();
            Log.d(TAG, "Deleted group: " + groupName + " and all related data");
        }
    }

    public static void syncWithFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
            .whereEqualTo("role", "student")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    String id = doc.getId();
                    String lastName = doc.getString("lastName");
                    String firstName = doc.getString("firstName");
                    String groupName = doc.getString("group");
                    Log.d(TAG, "Firebase student: " + lastName + " " + firstName + " group: '" + groupName + "'");
                    if (groupName != null) {
                        String normGroup = normalizeGroupName(groupName);
                        if (!groups.containsKey(normGroup)) {
                            Group group = new Group(groupName, groupName);
                            addGroup(group);
                            Log.d(TAG, "Added group from Firebase: '" + groupName + "' (normalized: '" + normGroup + "')");
                        }
                    }
                    if (lastName != null && firstName != null && groupName != null) {
                        if (!students.containsKey(id)) {
                            Student student = new Student(id, lastName, firstName, groupName);
                            addStudent(student);
                            Log.d(TAG, "Added student: " + lastName + " " + firstName + " to group: '" + groupName + "' (normalized: '" + normalizeGroupName(groupName) + "')");
                        }
                    }
                }
            });
    }

    private static void addDefaultGroup(String groupName) {
        for (String key : groups.keySet()) {
            if (key.trim().equalsIgnoreCase(groupName.trim())) return;
        }
        groups.put(groupName, new Group(groupName, groupName));
    }

    public static String normalizeGroupName(String name) {
        if (name == null) return "";
        // Заменяем латинские буквы на кириллические
        name = name.replace('A', 'А').replace('B', 'В').replace('V', 'В');
        name = name.replace('a', 'а').replace('b', 'в').replace('v', 'в');
        // Удаляем пробелы и приводим к верхнему регистру
        return name.trim().toUpperCase();
    }
} 