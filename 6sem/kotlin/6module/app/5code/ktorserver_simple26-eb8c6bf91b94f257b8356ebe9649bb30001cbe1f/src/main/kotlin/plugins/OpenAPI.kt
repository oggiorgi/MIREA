package org.example.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.* // Импортируем пакет с openAPI функцией
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureOpenAPI() {
    routing {
        // 1. ЭТО ГЛАВНОЕ: Создаем endpoint, который будет отдавать наш YAML-файл.
        //    Теперь Swagger UI будет знать, откуда взять спецификацию.
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")

        // 2. Подключаем Swagger UI, который будет читать спецификацию по пути "/openapi"
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")

        // 3. Redoc (опционально) будет читать спецификацию по тому же пути
        // redoc(path = "redoc", swaggerFile = "openapi/documentation.yaml")
    }
}