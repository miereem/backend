-- SQL запрос для добавления администратора в базу данных
-- 
-- ВАЖНО: Перед выполнением нужно сгенерировать BCrypt хеш для пароля
-- 
-- Вариант 1: Использовать онлайн генератор BCrypt (например, https://bcrypt-generator.com/)
-- Вариант 2: Использовать Java код для генерации (см. ниже)
-- 
-- Пример пароля: "admin123"
-- Пример BCrypt хеша: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- 1. Добавляем пользователя (замените 'YOUR_BCRYPT_HASH' на реальный хеш пароля)
INSERT INTO users (username, password)
VALUES ('admin', 'YOUR_BCRYPT_HASH')
ON CONFLICT (username) DO NOTHING;

-- 2. Добавляем роль ADMIN для пользователя
-- (замените ID на реальный ID пользователя, если нужно)
INSERT INTO users_roles (user_id, roles)
SELECT id, 'ADMIN'
FROM users
WHERE username = 'admin'
ON CONFLICT DO NOTHING;

-- Альтернативный вариант (если знаете ID пользователя):
-- INSERT INTO users_roles (user_id, roles) VALUES (1, 'ADMIN');

-- Проверка:
-- SELECT u.id, u.username, ur.roles FROM users u 
-- LEFT JOIN users_roles ur ON u.id = ur.user_id 
-- WHERE u.username = 'admin';

