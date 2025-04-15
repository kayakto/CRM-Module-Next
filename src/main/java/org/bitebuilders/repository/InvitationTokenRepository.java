package org.bitebuilders.repository;

import org.bitebuilders.model.InvitationToken;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InvitationTokenRepository  extends CrudRepository<InvitationToken, Long> {
    @Query("SELECT * FROM invitations " +
            "WHERE token = :token;")
    Optional<InvitationToken> findByToken(String token);
}
