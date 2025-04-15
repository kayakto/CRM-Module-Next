package org.bitebuilders.service;

import org.bitebuilders.enums.UserRole;
import org.bitebuilders.model.InvitationToken;
import org.bitebuilders.repository.InvitationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class InvitationTokenService {

    private final InvitationTokenRepository tokenRepository;

    private final JwtService jwtService;

    @Autowired
    public InvitationTokenService(InvitationTokenRepository tokenRepository, JwtService jwtService) {
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
    }

    // Генерация уникальной ссылки TODO запрос только от админа на регу рук
    public String generateToken(UserRole role, Long authorId) {
        InvitationToken token = new InvitationToken(
                jwtService.generateReferralToken(role, authorId),
                authorId,
                role,
                OffsetDateTime.now().plusDays(1) // Срок действия 1 день
        );
        tokenRepository.save(token);

        return token.getToken();
    }

    // Проверка токена
    public Optional<InvitationToken> validateToken(String token) {
        Optional<InvitationToken> invitationToken = tokenRepository.findByToken(token);

        if (invitationToken.isPresent()) {
            InvitationToken tokenEntity = invitationToken.get();

            // Проверяем, не истек ли срок действия
            if (tokenEntity.getExpirationDate().isBefore(OffsetDateTime.now()) || tokenEntity.isUsed()) {
                return Optional.empty();
            }

            return Optional.of(tokenEntity);
        }

        return Optional.empty();
    }

    // Отметить токен как использованный
    public void markTokenAsUsed(InvitationToken token) {
        token.setUsed(true);
        tokenRepository.save(token);
    }
}
