package org.bitebuilders.telegram.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TelegramUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public TelegramUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveIfNotExists(Long telegramId, String username, Long applicationId) {
        jdbcTemplate.update("""
            INSERT INTO telegram_users (telegram_id, telegram_username, application_id)
            VALUES (?, ?, ?)
            ON CONFLICT (telegram_id) DO NOTHING
        """, telegramId, username, applicationId);
    }
}
