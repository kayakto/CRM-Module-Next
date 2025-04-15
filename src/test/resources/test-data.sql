-- Удаление данных
DELETE FROM events_students;
DELETE FROM events_curators;
DELETE FROM event_groups;
DELETE FROM events;
DELETE FROM users_info;

-- Вставка событий
INSERT INTO events (condition, description_text, title, enrollment_start_date, enrollment_end_date, event_start_date, event_end_date, number_seats_students)
VALUES ('REGISTRATION_OPEN', 'descriptionText', 'title', NOW() - INTERVAL '5 days', NOW() - INTERVAL '1 days', NOW() + INTERVAL '1 hours', NOW() + INTERVAL '30 days', 50);

-- Проверка вставки пользователя
INSERT INTO users_info (first_name, last_name, email, sign, telegram_url, vk_url, role_enum, competencies)
VALUES ('Jane', 'Smith', 'janesmith@example.com', 'Student Signature', 't.me/janesmith', NULL, 'STUDENT', 'Critical Thinking, Collaboration'),
       ('John', 'Doe', 'johndoe@example.com', 'Curator Signature', 't.me/johndoe', NULL, 'CURATOR', 'Leadership, Organization');

-- Убедиться, что пользователь вставлен
SELECT id FROM users_info WHERE email = 'johndoe@example.com'; -- Проверка

-- Вставка данных в events_curators
INSERT INTO events_curators (curator_id, event_id, curator_status)
VALUES (
    (SELECT id FROM users_info WHERE email = 'johndoe@example.com' LIMIT 1),
    (SELECT id FROM events ORDER BY id DESC LIMIT 1),
    'ADDED_IN_CHAT'
);

-- Вставка данных в events_students
INSERT INTO events_students (student_id, event_id, student_status)
VALUES ((SELECT id FROM users_info WHERE email = 'janesmith@example.com' LIMIT 1),
        (SELECT id FROM events ORDER BY id DESC LIMIT 1), 'ADDED_IN_CHAT');
