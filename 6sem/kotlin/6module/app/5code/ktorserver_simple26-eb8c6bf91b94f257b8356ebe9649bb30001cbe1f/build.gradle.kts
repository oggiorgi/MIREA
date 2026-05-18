plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "3.0.0"
    kotlin("plugin.serialization") version "2.0.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-auth-jvm:3.0.0")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:3.0.0")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("io.ktor:ktor-client-cio-jvm:3.0.0")

    // PostgreSQL драйвер
    implementation("org.postgresql:postgresql:42.7.3")

    // Exposed (Kotlin ORM для работы с БД)
    implementation("org.jetbrains.exposed:exposed-core:0.50.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.50.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.50.1")

    // HikariCP — пул соединений
    implementation("com.zaxxer:HikariCP:6.0.0")

    implementation("com.auth0:java-jwt:4.4.0")

    // Source: https://mvnrepository.com/artifact/io.ktor/ktor-server-call-logging-jvm
    implementation("io.ktor:ktor-server-call-logging-jvm:3.0.0")

    // Добавить для работы с БД через Exposed + HikariCP (уже есть)
    // Добавить для OpenAPI/Swagger
    implementation("io.ktor:ktor-server-status-pages-jvm:3.0.0")
    implementation("io.ktor:ktor-server-cors-jvm:3.0.0")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:3.0.0")

    // OpenAPI документация
    implementation("io.ktor:ktor-server-openapi:3.0.0")
    implementation("io.ktor:ktor-server-swagger-jvm:3.0.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(22)
}
application {
    mainClass.set("org.example.ApplicationKt")
}