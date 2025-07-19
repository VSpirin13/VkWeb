CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone_number VARCHAR(20),
    role VARCHAR(50) DEFAULT 'USER',
    image_url VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS ads (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price INT,
    user_id INT NOT NULL,
    image_url VARCHAR(255),
    city VARCHAR(255),
    phone_number VARCHAR(255) NOT NULL DEFAULT '',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


INSERT INTO users (username, password, first_name, last_name, phone_number, role) VALUES
('testuser', '$2a$10$SOME_UNIQUE_GENERATED_HASH_1', 'Test', 'User', '1234567890', 'USER'),
('adminuser', '$2a$10$SOME_UNIQUE_GENERATED_HASH_2', 'Admin', 'User', '0987654321', 'ADMIN');


INSERT INTO ads (title, description, price, user_id, city, image_url, phone_number) VALUES
('Тестовое объявление 1', 'Это первое тестовое объявление.', 1000, 1, 'Город А', 'https://imagetest.hb.ru-msk.vkcloud-storage.ru/test_image.jpg', '111-222-3333'),
('Тестовое объявление 2', 'Это второе тестовое объявление.', 2500, 2, 'Город Б', 'https://imagetest.hb.ru-msk.vkcloud-storage.ru/test_image.jpg', '444-555-6666');


CREATE INDEX IF NOT EXISTS idx_ads_user_id ON ads (user_id);
CREATE INDEX IF NOT EXISTS idx_ads_city ON ads (city);
