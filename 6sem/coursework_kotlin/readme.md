# MuseFlow — Android-клиент музыкального стриминга

**Курсовая работа по дисциплине «Разработка клиент-серверных мобильных приложений»**

Клиентское Android-приложение для потокового воспроизведения музыки с поддержкой офлайн-режима, плейлистов и фонового воспроизведения.

---

## О проекте

MuseFlow — это современный музыкальный плеер, позволяющий слушать треки, создавать плейлисты, искать музыку по жанрам и исполнителям.
Приложение поддерживает работу без интернета за счёт кэширования треков и имеет полноценный фоновый плеер с уведомлением.

---

## Стек технологий

| Слой | Технология |
|------|------------|
| **Язык** | Kotlin |
| **UI** | Jetpack Compose + Material 3 |
| **Архитектура** | Clean Architecture |
| **DI** | Hilt |
| **Навигация** | `androidx.navigation:navigation-compose` |
| **Сеть** | Retrofit + OkHttp + Gson |
| **Аудио** | Media3 ExoPlayer + Media3 Session |
| **Локальное хранилище** | Room (SQLite) |
| **Аутентификация** | JWT (собственный бэкенд) |
| **Асинхронность** | Kotlin Coroutines + Flow |
| **Загрузка изображений** | Coil |

---

## Экраны и функционал

| Экран | Описание |
|-------|----------|
| **Login / Register** | Вход и регистрация через собственный бэкенд с JWT-токенами |
| **Catalog** | Каталог треков с группировкой по жанрам, блок «Треки дня» (плейлист из 10 случайных треков), поиск с сохранением истории |
| **Player** | Полноценный плеер: обложка, название, исполнитель, прогресс-бар, play/pause, следующий/предыдущий трек |
| **Playlists** | Список плейлистов пользователя, создание нового плейлиста, удаление |
| **Profile** | Информация о пользователе, выход из аккаунта, очистка кэша, переключение темы (светлая/тёмная) |
| **Genre Tracks** | Все треки выбранного жанра с возможностью запуска воспроизведения |

---
## Структура проекта (Clean Architecture)

```
com.example.museflow/

├── data/                                                     — Слой данных
│   ├── local/                                                — Локальное хранилище (кэш)
│   │   ├── dao/                                              — Запросы к базе данных
│   │   │   └── TrackDao.kt                                   — Сохранение и получение треков из Room
│   │   ├── entities/                                         — Таблицы базы данных
│   │   │   └── TrackEntity.kt                                — Таблица треков + преобразование в модель
│   │   └── MuseFlowDatabase.kt                               — Инициализация Room
│   ├── network/                                              — Работа с сервером
│   │   ├── api/                                              — Описание запросов к серверу
│   │   │   └── ApiService.kt                                 — Список всех эндпоинтов
│   │   ├── auth/                                             — Авторизация
│   │   │   └── TokenManager.kt                               — Сохранение и получение JWT токена
│   │   ├── client/                                           — Настройка HTTP
│   │   │   └── RetrofitClient.kt                             — Retrofit + автоподстановка токена
│   │   ├── models/                                           — Форматы данных для сервера
│   │   │   ├── AuthRequest.kt                                — Запросы логина/регистрации + ответ сервера
│   │   │   ├── PlaylistDto.kt                                — Как сервер отдаёт плейлист + перевод в модель
│   │   │   ├── Requests.kt                                   — Тела запросов (создать плейлист и тд)
│   │   │   └── TrackDto.kt                                   — Как сервер отдаёт трек + перевод в модель
│   └── repository/                                           — Реализация репозиториев
│       ├── AuthRepositoryImpl.kt                             — Логин/регистрация через API + сохранение токена
│       ├── PlaylistsRepositoryImpl.kt                        — Плейлисты: CRUD + добавление/удаление треков
│       └── TracksRepositoryImpl.kt                           — Треки: получить все/поиск + сохранение в кэш
│
├── di/                                                       — Внедрение зависимостей (Hilt)
│   ├── DatabaseModule.kt                                     — Как создать базу данных Room
│   ├── NetworkModule.kt                                      — Как создать Retrofit и TokenManager
│   ├── RepositoryModule.kt                                   — Как создать репозитории
│   └── UseCaseModule.kt                                      — Как создать UseCase'ы
│
├── domain/                                                   — Бизнес-логика (чистый Kotlin)
│   ├── models/                                               — Что такое пользователь, трек, плейлист
│   │   ├── Playlist.kt                                       — Модель плейлиста (id, название, треки)
│   │   ├── Track.kt                                          — Модель трека (id, название, исполнитель)
│   │   └── User.kt                                           — Модель пользователя (id, логин, email)
│   ├── repository/                                           — Что умеют репозитории (интерфейсы)
│   │   ├── AuthRepository.kt                                 — Функции: login, register
│   │   ├── PlaylistsRepository.kt                            — Функции: получить/создать/удалить/добавить
│   │   └── TracksRepository.kt                               — Функции: получить треки, поиск
│   └── usecase/                                              — Конкретные действия (вызываются из ViewModel)
│       ├── AddTrackToPlaylistUseCase.kt                      — Добавить трек в плейлист
│       ├── CreatePlaylistUseCase.kt                          — Создать новый плейлист
│       ├── DeletePlaylistUseCase.kt                          — Удалить плейлист
│       ├── GetPlaylistsUseCase.kt                            — Загрузить все плейлисты пользователя
│       ├── GetTracksUseCase.kt                               — Загрузить все треки (с кэшем)
│       ├── LoginUseCase.kt                                   — Войти в аккаунт
│       ├── RegisterUseCase.kt                                — Зарегистрироваться
│       ├── RemoveTrackFromPlaylistUseCase.kt                 — Удалить трек из плейлиста
│       └── SearchTracksUseCase.kt                            — Найти треки по запросу
│
├── presentation/                                             — Экранчики и интерфейс
│   └── ui/
│       ├── auth/                                             — Вход и регистрация
│       │   ├── AuthScreen.kt                                 — Формы ввода логина/пароля/email
│       │   └── AuthViewModel.kt                              — Обработка нажатий + запросы на сервер
│       ├── catalog/                                          — Главный экран с треками
│       │   ├── AddToPlaylistDialog.kt                        — Окно выбора плейлиста
│       │   ├── CatalogScreen.kt                              — Поиск, жанры, треки дня
│       │   └── CatalogViewModel.kt                           — Загрузка треков и поиск
│       ├── genre/                                            — Все треки одного жанра
│       │   └── GenreTracksScreen.kt                          — Список треков жанра + запуск плеера
│       ├── main/                                             — Главный экран с нижним меню
│       │   ├── MainScreen.kt                                 — Нижняя навигация
│       │   └── ProfileScreen.kt                              — Информация о пользователе, выход, тема
│       ├── player/                                           — Музыкальный плеер
│       │   └── PlayerScreen.kt                               — Обложка, кнопки, прогресс-бар
│       └── playlists/                                        — Плейлисты пользователя
│           ├── PlaylistDetailScreen.kt                       — Содержимое плейлиста (список треков)
│           ├── PlaylistsScreen.kt                            — Список плейлистов + кнопка создать
│           └── PlaylistsViewModel.kt                         — Загрузка/создание/удаление плейлистов
│
├── services/                                                 — Фоновые сервисы
│   └── PlaybackService.kt                                    — Плеер, который работает в фоне + уведомление
│
├── utils/                                                    — Вспомогательные функции
│   └── FormatUtils.kt                                        — Форматирование времени и склонение числительных
│
├── MainActivity.kt                                           — Точка входа: проверка токена и запуск экрана
└── MuseFlowApplication.kt                                    — Application класс для Hilt
```
## Описание файлов проекта

### data/ — Слой данных

#### local/ — Локальное хранилище (Room)

| Файл | Описание |
|------|----------|
| `dao/TrackDao.kt` | DAO для работы с треками в Room: получение всех треков, поиск, вставка, удаление, подсчёт количества |
| `entities/TrackEntity.kt` | Room-сущность трека с полями: id, title, artist, duration, coverUrl, audioUrl, genre. Содержит функции маппинга в domain-модель |
| `MuseFlowDatabase.kt` | База данных Room с таблицей треков. Реализует синглтон для доступа к БД |

#### network/ — Сетевой слой (Retrofit)

| Файл | Описание |
|------|----------|
| `api/ApiService.kt` | Интерфейс Retrofit API. Описывает все эндпоинты: логин, регистрация, получение треков, поиск, CRUD плейлистов |
| `auth/TokenManager.kt` | Менеджер для безопасного хранения JWT-токена и имени пользователя в SharedPreferences |
| `client/RetrofitClient.kt` | Фабрика для создания Retrofit-клиента. Настраивает OkHttp с интерцептором для добавления Bearer-токена и логирования |
| `models/AuthRequest.kt` | DTO для запросов аутентификации: LoginRequest (login, password) и RegisterRequest (login, email, password) |
| `models/PlaylistDto.kt` | DTO для передачи данных плейлиста с сервера: id, userId, name, coverUrl, createdAt, tracks |
| `models/Requests.kt` | DTO для запросов к API плейлистов: CreatePlaylistRequest, UpdatePlaylistRequest, AddTrackRequest |
| `models/TrackDto.kt` | DTO для передачи данных трека с сервера: id, title, artist, duration, coverUrl, audioUrl, genre |

#### repository/ — Реализации репозиториев

| Файл | Описание |
|------|----------|
| `AuthRepositoryImpl.kt` | Реализация репозитория авторизации. Выполняет вход и регистрацию, сохраняет токен. При регистрации автоматически выполняет вход |
| `PlaylistsRepositoryImpl.kt` | Реализация репозитория плейлистов. Управляет созданием, удалением, обновлением плейлистов. Обрабатывает HTTP-ошибки (409 Conflict) |
| `TracksRepositoryImpl.kt` | Реализация репозитория треков. Реализует стратегию "сначала сеть, потом кэш" для офлайн-режима. Поддерживает поиск с падением на локальную БД |

---

### di/ — Dagger Hilt модули

| Файл | Описание |
|------|----------|
| `DatabaseModule.kt` | Модуль для внедрения зависимостей базы данных Room. Предоставляет экземпляр MuseFlowDatabase и TrackDao |
| `NetworkModule.kt` | Модуль для внедрения сетевых зависимостей. Предоставляет TokenManager и ApiService |
| `RepositoryModule.kt` | Модуль для внедрения репозиториев. Предоставляет реализации AuthRepository, TracksRepository, PlaylistsRepository |
| `UseCaseModule.kt` | Модуль для внедрения UseCase'ов. Предоставляет все UseCase для бизнес-логики |

---

### domain/ — Доменный слой

#### models/ — Domain-модели

| Файл | Описание |
|------|----------|
| `Playlist.kt` | Доменная модель плейлиста: id, name, coverUrl, tracks. Используется в UI и бизнес-логике |
| `Track.kt` | Доменная модель трека с аннотацией @Parcelize для передачи между экранами. Поля: id, title, artist, duration, coverUrl, audioUrl, genre |
| `User.kt` | Доменная модель пользователя: id, login, email |

#### repository/ — Интерфейсы репозиториев

| Файл | Описание |
|------|----------|
| `AuthRepository.kt` | Интерфейс репозитория авторизации. Методы: login, register |
| `PlaylistsRepository.kt` | Интерфейс репозитория плейлистов. Методы: getPlaylists, createPlaylist, updatePlaylist, deletePlaylist, addTrackToPlaylist, removeTrackFromPlaylist |
| `TracksRepository.kt` | Интерфейс репозитория треков. Методы: getTracks, searchTracks, clearCache |

#### usecase/ — UseCase'ы (бизнес-логика)

| Файл | Описание |
|------|----------|
| `LoginUseCase.kt` | UseCase для входа пользователя. Вызывает AuthRepository.login |
| `RegisterUseCase.kt` | UseCase для регистрации пользователя. Вызывает AuthRepository.register |
| `GetTracksUseCase.kt` | UseCase для получения всех треков. Вызывает TracksRepository.getTracks |
| `SearchTracksUseCase.kt` | UseCase для поиска треков. Вызывает TracksRepository.searchTracks |
| `GetPlaylistsUseCase.kt` | UseCase для получения плейлистов. Вызывает PlaylistsRepository.getPlaylists |
| `CreatePlaylistUseCase.kt` | UseCase для создания плейлиста. Вызывает PlaylistsRepository.createPlaylist |
| `DeletePlaylistUseCase.kt` | UseCase для удаления плейлиста. Вызывает PlaylistsRepository.deletePlaylist |
| `AddTrackToPlaylistUseCase.kt` | UseCase для добавления трека в плейлист. Возвращает Boolean (успех/конфликт) |
| `RemoveTrackFromPlaylistUseCase.kt` | UseCase для удаления трека из плейлиста |

---

### presentation/ — Слой представления

#### ui/auth/ — Авторизация

| Файл | Описание |
|------|----------|
| `AuthScreen.kt` | Экран входа и регистрации. Поддерживает переключение между режимами. Валидация полей и обработка состояний (Loading, Success, Error) |
| `AuthViewModel.kt` | ViewModel для экрана авторизации. Управляет состоянием, вызывает LoginUseCase и RegisterUseCase. Обрабатывает ошибки (400 — неверные учётные данные) |

#### ui/catalog/ — Каталог треков

| Файл | Описание |
|------|----------|
| `CatalogScreen.kt` | Главный экран каталога. Содержит поиск с историей, блок "Треки дня" (горизонтальный карусель с 10 случайными треками), группировку треков по жанрам |
| `CatalogViewModel.kt` | ViewModel для каталога. Загружает треки, выполняет поиск, группирует по жанрам. Реализует стратегию кэширования |
| `AddToPlaylistDialog.kt` | Диалоговое окно для добавления трека в плейлист. Отображает список плейлистов пользователя |

#### ui/genre/ — Треки по жанру

| Файл | Описание |
|------|----------|
| `GenreTracksScreen.kt` | Экран со списком всех треков выбранного жанра. Поддерживает запуск воспроизведения с любого трека |

#### ui/main/ — Главный экран

| Файл | Описание |
|------|----------|
| `MainScreen.kt` | Главный экран с Bottom Navigation. Содержит NavHost для переключения между каталогом, плейлистами и профилем. Управляет навигацией к плееру |
| `ProfileScreen.kt` | Экран профиля пользователя. Отображает имя пользователя, статус авторизации. Позволяет выйти из аккаунта, очистить кэш, переключить тему (светлая/тёмная) |

#### ui/player/ — Плеер

| Файл | Описание |
|------|----------|
| `PlayerScreen.kt` | Экран аудиоплеера. Привязывается к PlaybackService, отображает прогресс-воспроизведения, кнопки управления (play/pause, next, previous). Автоматически синхронизирует состояние |

#### ui/playlists/ — Плейлисты

| Файл | Описание |
|------|----------|
| `PlaylistsScreen.kt` | Экран списка плейлистов. Поддерживает создание нового плейлиста (диалог) и удаление существующих |
| `PlaylistDetailScreen.kt` | Экран деталей плейлиста. Отображает список треков с возможностью удаления. При нажатии на трек запускает плеер |
| `PlaylistsViewModel.kt` | ViewModel для плейлистов. Управляет загрузкой, созданием, удалением плейлистов. Реализует оптимистичное обновление UI при удалении треков |

---

### services/ — Фоновые сервисы

| Файл | Описание |
|------|----------|
| `PlaybackService.kt` | Фоновый сервис для воспроизведения музыки. Использует ExoPlayer и Media3 Session. Поддерживает управление из уведомления. Реализует Service Binding для синхронизации с UI. Методы: play/pause, next/previous, seekTo, получение текущей позиции и длительности |

---

### ui/theme/ — UI темы

| Файл | Описание |
|------|----------|
| `Color.kt` | Цветовая палитра приложения для светлой и тёмной темы |
| `Theme.kt` | Настройка темы Compose. Определяет светлую и тёмную тему с использованием Material 3 |
| `Type.kt` | Типографика приложения: размеры шрифтов, начертания, межстрочные интервалы |

---

### utils/ — Утилиты

| Файл | Описание |
|------|----------|
| `FormatUtils.kt` | Утилита: getTracksText (правильное склонение слова "трек") |

---

### Корневые файлы

| Файл | Описание |
|------|----------|
| `MainActivity.kt` | Главная Activity приложения. При запуске проверяет наличие JWT-токена в SharedPreferences. Если токен есть — переходит на главный экран (MainScreen), иначе — на экран авторизации (AuthScreen). Управляет темой, запрашивает разрешение на уведомления (Android 13+). Обрабатывает выход из аккаунта (очистка токена, остановка сервиса, сброс ViewModel) |
| `MuseFlowApplication.kt` | Класс приложения с аннотацией @HiltAndroidApp. Инициализирует Dagger Hilt |


---


## MuseFlow — Backend (серверная часть)

Серверная часть приложения MuseFlow разработана на языке Kotlin с использованием фреймворка Ktor. Сервер реализует REST API для взаимодействия с клиентским приложением, обеспечивает аутентификацию пользователей через JWT, хранение музыкальных треков и управление плейлистами.

---

## Стек технологий

| Компонент | Технология | Назначение |
|-----------|------------|------------|
| **Язык** | Kotlin | Разработка серверной логики |
| **Фреймворк** | Ktor 3.0.0 | Создание REST API |
| **СУБД** | PostgreSQL (Neon.tech) | Хранение данных |
| **ORM** | Exposed 0.50.1 | Взаимодействие с БД через типобезопасные запросы |
| **Аутентификация** | JWT (Auth0 java-jwt 4.4.0) | Генерация и проверка токенов |
| **Хеширование паролей** | BCrypt (jBCrypt 0.4) | Безопасное хранение паролей |
| **Сериализация** | Kotlinx.serialization | Преобразование JSON-данных |
| **Пул соединений** | HikariCP 5.1.0 | Управление подключениями к БД |
| **Логирование** | Logback 1.5.6 | Логирование запросов и ошибок |
| **CORS** | Ktor CORS plugin | Разрешение кросс-доменных запросов |

---

## Структура проекта

```
src/main/kotlin/
├── Application.kt                                 — Точка входа, подключение к БД, CORS, статические файлы
│
├── database/                                      — Слой работы с базой данных
│   ├── users/                                     — Пользователи
│   │   ├── UserDTO.kt                             — DTO для передачи данных пользователя
│   │   └── Users.kt                               — Таблица users + CRUD операции
│   ├── tracks/                                    — Треки
│   │   ├── TrackDTO.kt                            — DTO для передачи данных трека
│   │   └── Tracks.kt                              — Таблица tracks + CRUD операции
│   └── playlists/                                 — Плейлисты
│       ├── PlaylistDTO.kt                         — DTO для передачи данных плейлиста
│       └── Playlists.kt                           — Таблицы playlists + playlist_tracks, CRUD операции
│
├── features/                                      — Бизнес-логика и маршруты
│   ├── login/                                     — Авторизация
│   │   ├── LoginController.kt                     — Контроллер: проверка учётных данных, генерация JWT
│   │   ├── LoginRemoteModel.kt                    — DTO для запроса/ответа логина
│   │   └── LoginRouting.kt                        — Маршрут POST /login
│   ├── register/                                  — Регистрация
│   │   ├── RegisterController.kt                  — Контроллер: валидация, создание пользователя
│   │   ├── RegisterRemote.kt                      — DTO для запроса/ответа регистрации
│   │   └── RegisterRouting.kt                     — Маршрут POST /register
│   ├── playlists/                                 — Управление плейлистами
│   │   └── PlaylistsRouting.kt                    — Маршруты для плейлистов (GET, POST, DELETE)
│   └── tracks/                                    — Управление треками
│       └── TracksRouting.kt                       — Маршруты для треков (GET, поиск)
│
├── routing/                                       — Общие маршруты и настройки
│   ├── Routing.kt                                 — Корневой маршрут GET /
│   ├── Serialization.kt                           — Настройка JSON-сериализации
│   └── UserRouting.kt                             — Маршрут GET /user/me (получение профиля)
│
└── utils/                                         — Вспомогательные утилиты
    ├── AuthUtils.kt                               — Извлечение userId из JWT-токена
    ├── CallLogging.kt                             — Настройка логирования запросов
    ├── EmailValidator.kt                          — Валидация email
    ├── JWTConfig.kt                               — Настройка JWT-аутентификации
    └── PasswordHasher.kt                          — Хеширование и проверка паролей (BCrypt)
```

---

## Описание файлов серверной части

### Корневой файл

| Файл | Описание |
|------|----------|
| `Application.kt` | Точка входа в приложение. Настраивает CORS, JWT, сериализацию, статические файлы (uploads/audio, uploads/covers). Подключает все маршруты (login, register, tracks, playlists, user). Запускает Ktor-сервер на порту 8080 |

---

### database/ — База данных

#### users/ — Пользователи

| Файл | Описание |
|------|----------|
| `UserDTO.kt` | Data Transfer Object для пользователя. Содержит поля: id, login, email, passwordHash, createdAt. Используется для передачи данных между сервером и клиентом |
| `Users.kt` | Exposed-таблица "users". Поля: id (автоинкремент), login (уникальный), email (уникальный), passwordHash, createdAt. Методы: fetchUser (поиск по логину), fetchUserByEmail, create (создание пользователя), getById |

#### tracks/ — Треки

| Файл | Описание |
|------|----------|
| `TrackDTO.kt` | DTO для трека. Поля: id, title, artist, duration, coverUrl, audioUrl, genre |
| `Tracks.kt` | Exposed-таблица "tracks". Поля: id (автоинкремент), title, artist, duration, coverUrl, audioUrl, genre. Методы: getAll (все треки), getById, search (поиск по title или artist) |

#### playlists/ — Плейлисты

| Файл | Описание |
|------|----------|
| `PlaylistDTO.kt` | DTO для плейлиста. Поля: id, userId, name, coverUrl, createdAt, tracks (список TrackDTO) |
| `Playlists.kt` | Exposed-таблицы "playlists" и "playlist_tracks". Таблица playlists: id, userId (внешний ключ → users), name, coverUrl, createdAt. Таблица playlist_tracks: playlistId, trackId, orderIndex. Методы: create, getById, getUserPlaylists, delete, updateName, addTrack, removeTrack |

---

### features/ — Маршруты и контроллеры

#### login/ — Авторизация

| Файл | Описание |
|------|----------|
| `LoginController.kt` | Контроллер авторизации. Получает login/password из запроса, ищет пользователя в БД, проверяет пароль через BCrypt. При успехе генерирует JWT-токен (срок действия 7 дней) |
| `LoginRemoteModel.kt` | DTO для запроса (login, password) и ответа (token) |
| `LoginRouting.kt` | Определяет маршрут POST /login |

#### register/ — Регистрация

| Файл | Описание |
|------|----------|
| `RegisterController.kt` | Контроллер регистрации. Валидирует email, проверяет существование пользователя, хеширует пароль через BCrypt, создаёт запись в БД |
| `RegisterRemote.kt` | DTO для запроса (login, email, password) и ответа (message) |
| `RegisterRouting.kt` | Определяет маршрут POST /register |

#### playlists/ — Управление плейлистами

| Файл | Описание |
|------|----------|
| `PlaylistsRouting.kt` | Маршруты для работы с плейлистами (все требуют JWT-аутентификацию): GET /playlists (получение всех плейлистов пользователя), POST /playlists (создание), DELETE /playlists/{id} (удаление), POST /playlists/{id}/tracks (добавление трека), DELETE /playlists/{id}/tracks/{trackId} (удаление трека). При конфликте (плейлист с таким именем уже существует) возвращает 409 |

#### tracks/ — Управление треками

| Файл | Описание |
|------|----------|
| `TracksRouting.kt` | Маршруты для работы с треками (требуют JWT-аутентификацию): GET /tracks (все треки), GET /tracks/search?q= (поиск по названию или исполнителю), GET /tracks/{id} (получение конкретного трека) |

---

### routing/ — Общие маршруты

| Файл | Описание |
|------|----------|
| `Routing.kt` | Корневой маршрут GET / — возвращает "hello, world" для проверки работоспособности сервера |
| `Serialization.kt` | Настройка JSON-сериализации через kotlinx.serialization. Включает prettyPrint, ignoreUnknownKeys, isLenient, encodeDefaults |
| `UserRouting.kt` | Маршрут GET /user/me (требует JWT). Возвращает id, login, email текущего пользователя |

---

### utils/ — Утилиты

| Файл | Описание |
|------|----------|
| `AuthUtils.kt` | Функция getUserIdFromToken — извлекает userId из JWT-токена, переданного в заголовке Authorization |
| `CallLogging.kt` | Настройка логирования HTTP-запросов (уровень INFO) |
| `EmailValidator.kt` | Валидация email (в текущей реализации всегда возвращает true) |
| `JWTConfig.kt` | Настройка JWT-аутентификации для Ktor. Использует секретный ключ (из переменной окружения или значение по умолчанию), алгоритм HMAC256, валидирует токены |
| `PasswordHasher.kt` | Хеширование паролей через BCrypt (salt автоматически генерируется). Методы: hash (хеширование), verify (проверка пароля) |

---

## API Эндпоинты

| Метод | Эндпоинт | Аутентификация | Описание |
|-------|----------|----------------|----------|
| POST | `/login` | Нет | Авторизация пользователя, выдача JWT-токена |
| POST | `/register` | Нет | Регистрация нового пользователя |
| GET | `/tracks` | Bearer token | Получение списка всех треков |
| GET | `/tracks/search?q={query}` | Bearer token | Поиск треков по названию или исполнителю |
| GET | `/tracks/{id}` | Bearer token | Получение информации о конкретном треке |
| GET | `/playlists` | Bearer token | Получение всех плейлистов текущего пользователя |
| POST | `/playlists` | Bearer token | Создание нового плейлиста |
| DELETE | `/playlists/{id}` | Bearer token | Удаление плейлиста |
| POST | `/playlists/{id}/tracks` | Bearer token | Добавление трека в плейлист |
| DELETE | `/playlists/{id}/tracks/{trackId}` | Bearer token | Удаление трека из плейлиста |

**Base URL:** `http://localhost:8080/` (локальный запуск) / `http://<IP-адрес>:8080/` (доступ с других устройств)

**Аутентификация:** JWT-токен передаётся в заголовке `Authorization: Bearer <token>`

---
