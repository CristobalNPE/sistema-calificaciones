package dev.cnpe.m6finalsc.application.web.security;

import dev.cnpe.m6finalsc.application.service.CookieService;
import dev.cnpe.m6finalsc.application.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final CookieService cookieService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        SecurityUser userDetails = (SecurityUser) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        cookieService.addTokenCookie(response, token);
        response.sendRedirect("/students");
    }
}
