package dev.cnpe.m6finalsc.application.service;

import dev.cnpe.m6finalsc.application.dto.auth.AuthenticationRequest;
import dev.cnpe.m6finalsc.application.dto.auth.AuthenticationResponse;
import dev.cnpe.m6finalsc.application.dto.auth.RegistrationRequest;
import dev.cnpe.m6finalsc.application.security.CookieService;
import dev.cnpe.m6finalsc.application.security.JwtService;
import dev.cnpe.m6finalsc.application.security.SecurityUser;
import dev.cnpe.m6finalsc.domain.DomainException;
import dev.cnpe.m6finalsc.domain.model.User;
import dev.cnpe.m6finalsc.infrastructure.repo.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static dev.cnpe.m6finalsc.domain.DomainException.ErrorCode.INVALID_DATA;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CookieService cookieService;

    public AuthenticationResponse registerApi(RegistrationRequest request) {
        String token = generateTokenForNewUser(request);
        return AuthenticationResponse.withToken(token);
    }

    public void registerWeb(HttpServletResponse response, RegistrationRequest request) {
        String token = generateTokenForNewUser(request);
        cookieService.addTokenCookie(response, token);
    }

    public AuthenticationResponse authenticateApi(AuthenticationRequest request) {
        String token = generateTokenForExistingUser(request);
        return AuthenticationResponse.withToken(token);
    }

    private String generateTokenForNewUser(RegistrationRequest request) {
        validateRegistrationRequest(request);

        User user = User.builder()
                        .username(request.username())
                        .password(passwordEncoder.encode(request.password()))
                        .name(request.name())
                        .email(request.email())
                        .role(User.Role.USER)
                        .build();

        User registeredUser = userRepository.save(user);

        return jwtService.generateToken(SecurityUser.fromUser(registeredUser));
    }

    private String generateTokenForExistingUser(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        User user = userRepository.findByUsername(request.username())
                                  .orElseThrow(() -> new UsernameNotFoundException(
                                          "Usuario no existe: " + request.username()));

        return jwtService.generateToken(SecurityUser.fromUser(user));
    }

    private void validateRegistrationRequest(RegistrationRequest request) {
        if (!request.passwordsMatch()) {
            throw new DomainException(INVALID_DATA, "Las contraseñas no coinciden");
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new DomainException(INVALID_DATA, "El usuario ya existe");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DomainException(INVALID_DATA, "El correo ya se encuentra registrado");
        }
    }
}
