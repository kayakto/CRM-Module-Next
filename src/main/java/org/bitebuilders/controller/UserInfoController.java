package org.bitebuilders.controller;

import org.bitebuilders.component.UserContext;
import org.bitebuilders.controller.dto.MessageResponseDTO;
import org.bitebuilders.controller.dto.ReferralTokenDTO;
import org.bitebuilders.controller.dto.TokensDTO;
import org.bitebuilders.controller.dto.UserDTO;
import org.bitebuilders.controller.requests.EmailUpdateRequest;
import org.bitebuilders.controller.requests.PasswordUpdateRequest;
import org.bitebuilders.controller.requests.UserUpdateRequest;
import org.bitebuilders.enums.UserRole;
import org.bitebuilders.exception.UserNotFoundException;
import org.bitebuilders.model.UserInfo;
import org.bitebuilders.service.InvitationTokenService;
import org.bitebuilders.service.JwtService;
import org.bitebuilders.service.UserInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserInfoController {

    private final UserInfoService userInfoService;

    private final UserContext userContext;

    private final InvitationTokenService invitationService;

    private final JwtService jwtService;

    @Autowired
    public UserInfoController(UserInfoService userInfoService, UserContext userContext, InvitationTokenService invitationService, JwtService jwtService) {
        this.userInfoService = userInfoService;
        this.userContext = userContext;
        this.invitationService = invitationService;
        this.jwtService = jwtService;
    }

    /**
     * Получение данных текущего пользователя
     * @return Данные пользователя
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserInfo user = userContext.getCurrentUser();

        return ResponseEntity.ok(user.toUserDTO());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
//        UserInfo userRequested = userContext.getCurrentUser(); TODO add permissions

        UserInfo userResult = userInfoService.getUserInfo(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id + " + userId + " not found"));

        return ResponseEntity.ok(
                userResult.toUserDTO()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/my-role")
    public ResponseEntity<MessageResponseDTO> getMyRole() {
        UserRole role = userContext.getCurrentUser().getRole_enum();

        return ResponseEntity.ok(new MessageResponseDTO(role.name()));
    }

    /**
     * Обновление данных текущего пользователя
     * @param updateRequest данные пользователя с учетом изменений
     * @return Обновленные данные пользователя
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser(@RequestBody UserUpdateRequest updateRequest) {
        UserInfo user = userContext.getCurrentUser();

        // Обновление данных пользователя
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setSurname(updateRequest.getSurname());
        user.setTelegramUrl(updateRequest.getTelegramUrl());
        user.setVkUrl(updateRequest.getVkUrl());
        user.setUpdatedAt(OffsetDateTime.now());

        UserInfo updatedUser = userInfoService.addOrUpdateUser(user);

        return ResponseEntity.ok(updatedUser.toUserDTO());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update-password")
    public ResponseEntity<TokensDTO> updatePassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        String email = userContext.getCurrentUserEmail();
        UserInfo user = userInfoService.getByEmail(email);

        try {
            userInfoService.updatePassword(
                    user,
                    passwordUpdateRequest.getOldPassword(),
                    passwordUpdateRequest.getNewPassword());

            String newAccessToken = jwtService.generateToken(
                    email, user.getRole_enum().name());
            String newRefreshToken = jwtService.generateRefreshToken(
                    email, user.getRole_enum().name());

            return ResponseEntity.ok(
                    new TokensDTO(newAccessToken, newRefreshToken));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update-email")
    public ResponseEntity<TokensDTO> updateEmail(@RequestBody EmailUpdateRequest emailUpdateRequest) {
        UserInfo user = userContext.getCurrentUser();

        try {
            userInfoService.updateEmail(user, emailUpdateRequest.getNewEmail());
            String newAccessToken = jwtService.generateToken(
                    emailUpdateRequest.getNewEmail(), user.getRole_enum().name());
            String newRefreshToken = jwtService.generateRefreshToken(
                    emailUpdateRequest.getNewEmail(), user.getRole_enum().name());

            // Возвращаем новые токены
            return ResponseEntity.ok(new TokensDTO(newAccessToken, newRefreshToken));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/invite-admin")
    public ResponseEntity<ReferralTokenDTO> createReferralToken() {
        Long authorId = userContext.getCurrentUser().getId();

        String token = invitationService.generateToken(
                UserRole.ADMIN,
                authorId
        );

        return ResponseEntity.ok(new ReferralTokenDTO(token));
    }
}
