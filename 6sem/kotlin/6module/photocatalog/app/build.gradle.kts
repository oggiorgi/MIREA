plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.photocatalog"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.photocatalog"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Moshi с reflection
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

    // Coil для загрузки изображений
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Glide
    //implementation("com.github.bumptech.glide:glide:4.16.0")
    //implementation("com.github.bumptech.glide:compose:1.0.0-beta01")

    // Lifecycle & Coroutines
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.7")

    // ДЛЯ ЗАДАНИЯ: Сохранение файлов
    implementation("androidx.documentfile:documentfile:1.0.1")

    // Платформа Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))

    // Минимум для Hello World
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.13.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}