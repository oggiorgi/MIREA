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
| **Splash** | Проверяет наличие JWT-токена. Если есть — загружает данные и идёт в каталог, иначе — на экран авторизации |
| **Login / Register** | Вход и регистрация через собственный бэкенд с JWT-токенами |
| **Catalog** | Каталог треков с группировкой по жанрам, блок «Треки дня» (плейлист из 10 случайных треков), поиск с сохранением истории |
| **Player** | Полноценный плеер: обложка, название, исполнитель, прогресс-бар, play/pause, следующий/предыдущий трек |
| **Playlists** | Список плейлистов пользователя, создание нового плейлиста, удаление |
| **Playlist Detail** | Треки внутри плейлиста с возможностью удаления |
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
| `FormatUtils.kt` | Утилиты для форматирования: formatDuration (секунды → MM:SS), getTracksText (правильное склонение слова "трек") |

---

### Корневые файлы

| Файл | Описание |
|------|----------|
| `MainActivity.kt` | Главная Activity приложения. Настраивает навигацию (Splash → Auth/Main), управляет темой, запрашивает разрешение на уведомления (Android 13+). Обрабатывает выход из аккаунта (очистка токена, остановка сервиса, сброс ViewModel) |
| `MuseFlowApplication.kt` | Класс приложения с аннотацией @HiltAndroidApp. Инициализирует Dagger Hilt |

---

## 🔗 Взаимодействие с бэкендом

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| POST | `/login` | Вход (логин/пароль → JWT) |
| POST | `/register` | Регистрация (логин/email/пароль) |
| GET | `/tracks` | Получить все треки |
| GET | `/tracks/search?q=` | Поиск треков |
| GET | `/playlists` | Получить плейлисты пользователя |
| POST | `/playlists` | Создать плейлист |
| PUT | `/playlists/{id}` | Переименовать плейлист |
| DELETE | `/playlists/{id}` | Удалить плейлист |
| POST | `/playlists/{id}/tracks` | Добавить трек в плейлист |
| DELETE | `/playlists/{id}/tracks/{trackId}` | Удалить трек из плейлиста |

**Base URL:** `http://10.0.2.2:8080/` (эмулятор) / `http://<IP-компьютера>:8080/` (реальное устройство)

**Аутентификация:** JWT-токен передаётся в заголовке `Authorization: Bearer <token>`

---


