package com.example.electronicgradebook;

import android.app.Application;
import android.util.Log;
import java.util.List;
import java.util.Map;
import com.google.firebase.FirebaseApp;
import com.google.android.gms.common.GoogleApiAvailability;

public class ElectronicGradebookApp extends Application {
    private static final String TAG = "ElectronicGradebookApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate started");
        
        try {
            // Инициализация Firebase
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
                Log.d(TAG, "Firebase initialized successfully");
            }

            // Проверка доступности Google Play Services
            GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
            int resultCode = googleAPI.isGooglePlayServicesAvailable(this);
            if (resultCode != com.google.android.gms.common.ConnectionResult.SUCCESS) {
                Log.e(TAG, "Google Play Services not available: " + googleAPI.getErrorString(resultCode));
            } else {
                Log.d(TAG, "Google Play Services available");
            }
            
            Log.d(TAG, "Starting MockData initialization");
            MockData.init(this);
            MockData.syncWithFirebase();
            Log.d(TAG, "MockData initialized successfully");
            
            // Проверяем, что данные загружены
            Map<String, Group> groups = MockData.getGroups();
            Log.d(TAG, "Loaded " + groups.size() + " groups after initialization");
            
            for (Group group : groups.values()) {
                List<Student> students = MockData.getStudentsByGroup(group.getId());
                Log.d(TAG, "Group " + group.getName() + " has " + students.size() + " students");
                for (Student student : students) {
                    Log.d(TAG, "Student: " + student.getFullName() + " in group " + group.getName());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing app", e);
        }
    }
} 