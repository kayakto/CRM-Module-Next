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

CREATE TABLE application_statuses (
    id SERIAL PRIMARY KEY,
    event_id BIGINT REFERENCES events(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    is_system BOOLEAN DEFAULT FALSE,
    display_order INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uniq_status_order UNIQUE (event_id, display_order)
);

-- Создаем таблицу заявок в соответствии с Java-классом
CREATE TABLE applications (
    id SERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    surname VARCHAR(100),
    email VARCHAR(100) NOT NULL,
    telegram_url VARCHAR(255),
    status_id BIGINT NOT NULL REFERENCES application_statuses(id),
    form_data JSONB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


-- История статусов с причиной изменения
CREATE TABLE application_status_history (
    id SERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    from_status_id BIGINT REFERENCES application_statuses(id),
    to_status_id BIGINT NOT NULL REFERENCES application_statuses(id),
    changed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
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
    parameters JSONB NOT NULL DEFAULT '{}'::jsonb,
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

ALTER TABLE status_robots ADD COLUMN position INTEGER NOT NULL DEFAULT 0;

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


INSERT INTO application_statuses (name, is_system, display_order)
VALUES
    ('Отправил(а) заявку', TRUE, 1),
    ('В обработке', TRUE, 2),
    ('Одобрена', TRUE, 3),
    ('Отклонена', TRUE, 4);

INSERT INTO triggers (id, name, type, parameters)
VALUES
  (1, 'Отследить переход по ссылке из сообщения', 'LINK_CLICK', '{"link": ""}'),
  (2, 'Отследить результаты тестирования', 'TEST_RESULT', '{"condition": "", "value": 0}');

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

ALTER TABLE events ADD CONSTRAINT check_dates CHECK (
    enrollment_start_date <= enrollment_end_date AND
    enrollment_end_date <= event_start_date AND
    event_start_date <= event_end_date
);

-- Добавляем проверку для display_order
ALTER TABLE application_statuses ADD CONSTRAINT chk_display_order CHECK (display_order > 0);

GRANT ALL PRIVILEGES ON TABLE standard_fields TO crm_admin;
GRANT USAGE ON SEQUENCE standard_fields_id_seq TO crm_admin;

GRANT ALL PRIVILEGES ON TABLE applications TO crm_admin;
GRANT ALL PRIVILEGES ON SEQUENCE applications_id_seq TO crm_admin;


-- Удаляем отдельные колонки из applications и переносим их в form_data
ALTER TABLE applications
DROP COLUMN first_name,
DROP COLUMN last_name,
DROP COLUMN surname,
DROP COLUMN email,
DROP COLUMN telegram_url;

-- Создаем таблицу для системных полей
CREATE TABLE system_fields (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    is_required BOOLEAN DEFAULT TRUE,
    display_order INTEGER NOT NULL
);

-- Добавляем системные поля
INSERT INTO system_fields (name, type, is_required, display_order) VALUES
('last_name', 'text', true, 1),
('first_name', 'text', true, 2),
('surname', 'text', false, 3),
('email', 'email', true, 4),
('telegram_url', 'url', true, 5),
('vk_url', 'url', false, 6);

-- Добавляем связь между формами и системными полями
CREATE TABLE form_system_fields (
    form_id INT NOT NULL REFERENCES event_forms(id) ON DELETE CASCADE,
    system_field_id INT NOT NULL REFERENCES system_fields(id) ON DELETE CASCADE,
    is_required BOOLEAN DEFAULT TRUE,
    display_order INT NOT NULL,
    PRIMARY KEY (form_id, system_field_id)
);

CREATE TABLE application_trigger_executions (
    application_id INT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    status_id INT NOT NULL REFERENCES application_statuses(id) ON DELETE CASCADE,
    trigger_id INT NOT NULL REFERENCES triggers(id) ON DELETE CASCADE,
    executed BOOLEAN NOT NULL DEFAULT FALSE,
    executed_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (application_id, status_id, trigger_id)
);

CREATE TABLE telegram_users (
    id SERIAL PRIMARY KEY,
    telegram_id BIGINT NOT NULL UNIQUE,
    telegram_username TEXT,
    application_id BIGINT REFERENCES applications(id),
    created_at TIMESTAMP DEFAULT now()
);
