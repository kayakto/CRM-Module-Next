\c internships

-- Администраторы
CREATE TABLE users_info (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    surname VARCHAR(50),
    email VARCHAR(255) UNIQUE NOT NULL,
    sign VARCHAR(255) NOT NULL,
    vk_url VARCHAR(255),
    telegram_url VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    role_enum VARCHAR(20) DEFAULT 'ADMIN'
);

-- Мероприятия
CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PREPARATION',
    description TEXT,
    admin_id INT REFERENCES users_info(id) ON DELETE SET NULL,
    enrollment_start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    enrollment_end_date TIMESTAMP WITH TIME ZONE NOT NULL,
    event_start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    event_end_date TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    has_test BOOLEAN DEFAULT FALSE,
    number_seats_students INT NOT NULL
);

-- Формы мероприятий
CREATE TABLE event_forms (
    id SERIAL PRIMARY KEY,
    event_id INT REFERENCES events(id) ON DELETE CASCADE,
    title TEXT,
    is_template BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Поля форм
CREATE TABLE form_fields (
    id SERIAL PRIMARY KEY,
    form_id INT NOT NULL REFERENCES event_forms(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_required BOOLEAN DEFAULT FALSE,
    display_order INT NOT NULL DEFAULT 0,
    options JSONB
);

-- Статусы заявок
CREATE TABLE application_statuses (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    is_system BOOLEAN DEFAULT FALSE,
    display_order INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Заявки
CREATE TABLE applications (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    surname VARCHAR(50),
    email VARCHAR(255) NOT NULL,
    telegram_url VARCHAR(255),
    status_id INT REFERENCES application_statuses(id) ON DELETE SET NULL,
    form_data JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Триггеры
CREATE TABLE triggers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    parameters JSONB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Связь статусов и триггеров
CREATE TABLE status_triggers (
    status_id INT NOT NULL REFERENCES application_statuses(id) ON DELETE CASCADE,
    trigger_id INT NOT NULL REFERENCES triggers(id) ON DELETE CASCADE,
    executed BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (status_id, trigger_id)
);

-- Роботы
CREATE TABLE robots (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    parameters JSONB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Связь статусов и роботов
CREATE TABLE status_robots (
    status_id INT NOT NULL REFERENCES application_statuses(id) ON DELETE CASCADE,
    robot_id INT NOT NULL REFERENCES robots(id) ON DELETE CASCADE,
    executed_at TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (status_id, robot_id)
);

CREATE TABLE events_tests (
    id SERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    test_url TEXT NOT NULL,
    CONSTRAINT fk_event_test_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

CREATE TABLE student_test_results (
    id SERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    test_id BIGINT NOT NULL,
    passed BOOLEAN NOT NULL,
    score INT NOT NULL,
    CONSTRAINT fk_student_test_result_student FOREIGN KEY (student_id) REFERENCES users_info(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_test_result_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS invitations (
    id SERIAL PRIMARY KEY,
    token VARCHAR(512) NOT NULL,
    role_enum VARCHAR(20),
    expiration_date DATE NOT NULL,
    used BOOLEAN NOT NULL,
    author_id INT NOT NULL REFERENCES users_info(id) ON DELETE CASCADE
);

-- Индексы
CREATE INDEX idx_applications_event_id ON applications(event_id);
CREATE INDEX idx_applications_status_id ON applications(status_id);
CREATE INDEX idx_status_triggers ON status_triggers(status_id, trigger_id);

GRANT ALL PRIVILEGES ON DATABASE internships TO crm_admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO crm_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO crm_admin;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO crm_admin;


-- Добавляем администратора
INSERT INTO users_info (id, first_name, last_name, email, sign, telegram_url, vk_url, role_enum)
VALUES (1, 'admin', 'admin', 'admin@mail.ru',
        '$2a$10$Aa7zaiEhnXHHD3SbBTUxX.gM3eXmSiPXQlYKx9KqJaL2cJ1WcyZHy',
        't.me/admin', 'vk.com/admin', 'ADMIN');

-- Добавляем тестовое мероприятие
INSERT INTO events (title, status, description, admin_id,
                   enrollment_start_date, enrollment_end_date,
                   event_start_date, event_end_date, number_seats_students)
VALUES ('Тестовый проект', 'PREPARATION', 'Описание проекта', 1,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '7 days',
        CURRENT_TIMESTAMP + INTERVAL '14 days', CURRENT_TIMESTAMP + INTERVAL '21 days', 50);

-- Добавляем форму для мероприятия
INSERT INTO event_forms (event_id, title)
VALUES (1, 'Основная форма регистрации на проект');


-- Добавляем статусы заявок
INSERT INTO application_statuses (name, is_system, display_order)
VALUES
    ('Отправил(а) заявку', TRUE, 1),
    ('В обработке', TRUE, 2),
    ('Одобрена', TRUE, 3),
    ('Отклонена', TRUE, 4);

-- Добавляем тестовые заявки
INSERT INTO applications (event_id, first_name, last_name, email, status_id, form_data)
VALUES
    (1, 'Иван', 'Иванов', 'ivan@example.com', 1,
     '{"phone": "+79123456789", "resume": "ivan_resume.pdf"}'),
    (1, 'Петр', 'Петров', 'petr@example.com', 2,
     '{"phone": "+79876543210", "resume": "petr_cv.pdf"}');

-- Добавляем триггеры
INSERT INTO triggers (name, type, parameters)
VALUES
    ('Отправить приветственное письмо', 'email',
     '{"template": "welcome", "subject": "Добро пожаловать!"}'),
    ('Уведомить администратора', 'notification',
     '{"channel": "telegram", "message": "Новая заявка"}');

-- Связываем триггеры со статусами
INSERT INTO status_triggers (status_id, trigger_id)
VALUES (1, 1), (2, 2);

CREATE TABLE standard_fields (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_required BOOLEAN DEFAULT FALSE,
    display_order INTEGER,
    options JSONB
);

INSERT INTO standard_fields (name, type, is_required, display_order, options) VALUES
('Образовательное учреждение', 'text', true, 1, NULL),
('Факультет', 'text', true, 2, NULL),
('Направление подготовки', 'text', true, 3, NULL),
('Курс обучения', 'number', true, 4, NULL),
('Форма обучения', 'select', true, 5, '["Очная", "Заочная", "Очно-заочная"]'),
('Навыки и компетенции', 'textarea', false, 6, NULL),
('Опыт работы или стажировок', 'textarea', false, 7, NULL),
('Ссылка на портфолио / GitHub / резюме', 'url', false, 8, NULL);

