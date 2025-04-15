package org.bitebuilders.controller;

import org.bitebuilders.controller.dto.TokensDTO;
import org.bitebuilders.controller.dto.UserDTO;
import org.bitebuilders.controller.requests.LoginRequest;
import org.bitebuilders.controller.requests.RefreshTokenRequest;
import org.bitebuilders.controller.requests.UserRequest;
import org.bitebuilders.enums.UserRole;
import org.bitebuilders.model.InvitationToken;
import org.bitebuilders.model.UserInfo;
import org.bitebuilders.service.InvitationTokenService;
import org.bitebuilders.service.JwtService;
import org.bitebuilders.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserInfoService userInfoService;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final InvitationTokenService tokenService;

    @Autowired
    public AuthController(UserInfoService userInfoService, PasswordEncoder passwordEncoder, JwtService jwtService, InvitationTokenService tokenService) {
        this.userInfoService = userInfoService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
    }

    // Регистрация без токена
//    @PostMapping("/register")
//    public ResponseEntity<UserDTO> register(@RequestBody UserRequest userRequest) {
//        // Проверка обязательных полей
//        UserRole requestRole = userRequest.getRole();
//        if (!(requestRole == UserRole.STUDENT || requestRole == UserRole.CURATOR)) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        if (userRequest.getCompetencies() == null || userRequest.getCompetencies().isEmpty()) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        // Проверка существующего пользователя
//        if (userInfoService.existsByEmail(userRequest.getEmail())) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        // Создание пользователя
//        UserInfo userInfo = userRequest.toUserInfo(
//                requestRole,
//                passwordEncoder.encode(userRequest.getSign())
//        );
//
//        UserInfo savedUser = userInfoService.addOrUpdateUser(userInfo);
//
//        // Генерация URI для созданного пользователя
//        URI location = URI.create("/users/" + savedUser.getId());
//
//        return ResponseEntity.created(location).body(savedUser.toUserDTO());
//    }

    // Регистрация с токеном
//    @PostMapping("/register-with-token")
//    public ResponseEntity<UserDTO> registerWithToken(@RequestParam String token,
//                                                     @RequestBody UserRequest userRequest) {
//        Optional<InvitationToken> optionalToken = tokenService.validateToken(token);
//
//        if (optionalToken.isEmpty()) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        InvitationToken invitationToken = optionalToken.get();
//
//        // Проверка существующего пользователя
//        if (userInfoService.existsByEmail(userRequest.getEmail())) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        // Проверка на наличие запрещенных полей
//        if (userRequest.get() != null || userRequest.getCompetencies() != null) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        // Создание пользователя
//        UserInfo userInfo = userRequest.toUserInfo(
//                invitationToken.getRole(),
//                passwordEncoder.encode(userRequest.getSign())
//        );
//
//        UserInfo savedUser = userInfoService.addOrUpdateUser(userInfo);
//
//        // Отметка токена как использованного
//        tokenService.markTokenAsUsed(invitationToken);
//
//        // Генерация URI для созданного пользователя
//        URI location = URI.create("/users/" + savedUser.getId());
//
//        return ResponseEntity.created(location).body(savedUser.toUserDTO());
//    }

//    @Operation(summary = "Login", description = "Authenticate user and retrieve tokens", security = {})
    @PostMapping("/login")
    public ResponseEntity<TokensDTO> login(@RequestBody LoginRequest loginRequest) {
        UserInfo user = userInfoService.getByEmail(loginRequest.getEmail());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getSign())) {
            throw new BadCredentialsException("Invalid password");
        }

        String access = jwtService.generateToken(user.getEmail(), user.getRole_enum().name());
        String refresh = jwtService.generateRefreshToken(user.getEmail(), user.getRole_enum().name());

        return ResponseEntity.ok(new TokensDTO(access, refresh));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokensDTO> refresh(@RequestBody RefreshTokenRequest request) {
        String refresh = request.getRefresh();

        if (!jwtService.validateToken(refresh)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String username = jwtService.getUsernameFromToken(refresh);
        String role = jwtService.getRoleFromToken(refresh);
        String newAccess = jwtService.generateToken(username, role);

        return ResponseEntity.ok(new TokensDTO(newAccess, refresh));
    }
}

