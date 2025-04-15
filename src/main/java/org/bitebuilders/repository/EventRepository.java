package org.bitebuilders.repository;

import org.bitebuilders.model.Event;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
    // Метод для поиска по статусу
    @Query("SELECT * FROM events WHERE condition = :condition;")
    List<Event> findAllByStatus(Event.Status condition); // тут конвертер работает

    // Метод для поиска по adminId
    @Query("SELECT * FROM events WHERE admin_id = :adminId AND condition != 'DELETED';")
    List<Event> findAllByAdminId(Long adminId);

    @Query("SELECT * FROM events WHERE manager_id = :managerId AND condition != 'DELETED';")
    List<Event> findAllByManagerId(Long managerId);

    // Метод для удаления из базы данных удаленных пользователем записей,
    // которые были удалены им более 30 дней назад
    @Transactional
    @Modifying
    @Query("DELETE FROM events e WHERE e.condition = :condition AND e.updatedAt < :dateTime")
    void deleteByConditionAndUpdatedBefore(Event.Status condition, LocalDateTime dateTime);
    // TODO добавить events.updatedAt, это метод вызывает ошибку в полночь

    // Метод для поиска по дате начала мероприятия
    @Query("SELECT * FROM events WHERE event_start_date <= :dateTime AND condition = 'REGISTRATION_OPEN'")
    List<Event> findStartedEventsByDate(OffsetDateTime dateTime); // Todo мб еще закрытую регистрация проверить
}
