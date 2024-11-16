package dev.cnpe.m6finalsc.application.web.controller.rest;

import dev.cnpe.m6finalsc.application.dto.auth.AuthenticationRequest;
import dev.cnpe.m6finalsc.application.dto.auth.AuthenticationResponse;
import dev.cnpe.m6finalsc.application.dto.auth.RegistrationRequest;
import dev.cnpe.m6finalsc.application.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public AuthenticationResponse register(@RequestBody RegistrationRequest request) {
        return authenticationService.registerApi(request);
    }

    @PostMapping("/login")
    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request) {
        return authenticationService.authenticateApi(request);
    }
}
