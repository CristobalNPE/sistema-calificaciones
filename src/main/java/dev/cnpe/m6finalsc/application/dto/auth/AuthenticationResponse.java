package dev.cnpe.m6finalsc.application.dto.auth;

public record AuthenticationResponse(String token) {

    public static AuthenticationResponse withToken(String token) {
        return new AuthenticationResponse(token);
    }
}
