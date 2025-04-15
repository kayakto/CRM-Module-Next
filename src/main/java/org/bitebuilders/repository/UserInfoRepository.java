package org.bitebuilders.repository;

import org.bitebuilders.model.UserInfo;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInfoRepository extends CrudRepository<UserInfo, Long> {

    @Query("SELECT * " +
            "FROM users_info " +
            "WHERE email = :email;")
    Optional<UserInfo> findByEmail(String email);

    @Query("SELECT id, first_name, last_name, surname, email, telegram_url, vk_url " +
            "FROM users_info " +
            "WHERE role_enum = 'ADMIN';")
    List<UserInfo> findAllAdmins();
}

