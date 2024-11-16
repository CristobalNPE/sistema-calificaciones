package dev.cnpe.m6finalsc.application.dto.auth;

public record RegistrationRequest(
        String username,
        String password,
        String passwordConfirm,
        String name,
        String email
) {

    public boolean passwordsMatch() {
        return password.equals(passwordConfirm);
    }

    public static RegistrationRequest empty() {
        return new RegistrationRequest("", "", "", "", "");
    }

}
