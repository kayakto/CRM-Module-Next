package org.bitebuilders.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bitebuilders.controller.dto.UserDTO;
import org.bitebuilders.enums.UserRole;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;

import java.time.OffsetDateTime;

@Getter
@Table("users_info")
@Setter
@ToString
public class UserInfo {
    @Id
    private Long id;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("surname")
    private String surname;

    @Column("email")
    private String email;

    @Column("sign")
    private String sign;

    @Column("vk_url")
    private String vkUrl;

    @Column("role_enum")
    private UserRole role_enum;

    @Column("telegram_url")
    private String telegramUrl;

    @Column("created_at")
    private OffsetDateTime createdAt;

    @Column("updated_at")
    private OffsetDateTime updatedAt;

    public UserInfo(String firstName, String lastName, String surname, String email, String sign, String vkUrl, String telegramUrl, UserRole role_enum) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.surname = surname;
        this.email = email;
        this.sign = sign;
        this.vkUrl = vkUrl;
        this.telegramUrl = telegramUrl;
        this.role_enum = role_enum;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    public UserDTO toUserDTO() {
        return new UserDTO(
                this.id,
                this.firstName,
                this.lastName,
                this.surname,
                this.email,
                this.vkUrl,
                this.role_enum,
                this.telegramUrl,
                this.createdAt,
                this.updatedAt
        );
    }
}
