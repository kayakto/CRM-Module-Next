package org.bitebuilders.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bitebuilders.enums.UserRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.OffsetDateTime;

@Getter
@Table("invitations")
@ToString
public class InvitationToken {
    @Id
    private Long id;

    @Column("token")
    private String token; // Уникальный токен

    @Column("author_id")
    private Long authorId;

    @Column("role_enum")
    private UserRole role; // Роль, для которой предназначена ссылка

    @Column("expiration_date")
    private OffsetDateTime expirationDate; // Дата истечения срока действия

    @Column("used")
    @Setter
    private boolean used = false; // Флаг, использована ли ссылка

    public InvitationToken(String token, Long authorId, UserRole role, OffsetDateTime expirationDate) {
        this.token = token;
        this.authorId = authorId;
        this.role = role;
        this.expirationDate = expirationDate;
    }
}
