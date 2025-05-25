package org.bitebuilders.telegram.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TelegramUserRepository {
    private static final Logger logger = LoggerFactory.getLogger(TelegramUserRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public TelegramUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long findApplicationIdByTelegramId(String telegramId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT application_id FROM telegram_users WHERE telegram_id = ?",
                    new Object[]{telegramId}, Long.class);
        } catch (Exception e) {
            logger.warn("Telegram ID not found for telegramId {}: {}", telegramId, e.getMessage());
            return null;
        }
    }

    public String findTelegramIdByApplicationId(Long applicationId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT telegram_id FROM telegram_users WHERE application_id = ?",
                    new Object[]{applicationId}, String.class);
        } catch (Exception e) {
            logger.warn("Telegram ID not found for application {}: {}", applicationId, e.getMessage());
            return null;
        }
    }

    public void saveTelegramUser(String telegramId, String telegramUsername, Long applicationId) {
        try {
            jdbcTemplate.update(
                    """
                    INSERT INTO telegram_users (telegram_id, telegram_username, application_id)
                    VALUES (?, ?, ?)
                    ON CONFLICT (telegram_id, application_id) DO NOTHING
                    """,
                    telegramId, telegramUsername, applicationId);
            logger.debug("Saved telegram user: telegramId={}, username={}, applicationId={}",
                    telegramId, telegramUsername, applicationId);
        } catch (Exception e) {
            logger.error("Failed to save telegram user for telegramId {}, applicationId {}: {}",
                    telegramId, applicationId, e.getMessage(), e);
        }
    }
}