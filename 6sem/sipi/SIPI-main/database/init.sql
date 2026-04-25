-- Создание базы данных
CREATE DATABASE pex_db;

\c pex_db;

-- Таблица пользователей
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица категорий
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(10) CHECK (type IN ('income', 'expense')) NOT NULL,
    color VARCHAR(7) DEFAULT '#000000'
);

-- Таблица операций
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    category_id INTEGER REFERENCES categories(id) ON DELETE SET NULL,
    amount DECIMAL(12,2) NOT NULL CHECK (amount > 0),
    date DATE NOT NULL,
    description TEXT,
    type VARCHAR(10) CHECK (type IN ('income', 'expense')) NOT NULL
);

-- Таблица бюджетов
CREATE TABLE budgets (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    category_id INTEGER REFERENCES categories(id) ON DELETE CASCADE,
    month DATE NOT NULL,
    limit_amount DECIMAL(12,2) NOT NULL CHECK (limit_amount >= 0)
);

-- Индексы
CREATE INDEX idx_transactions_user_date ON transactions(user_id, date);
CREATE INDEX idx_users_email ON users(email);
