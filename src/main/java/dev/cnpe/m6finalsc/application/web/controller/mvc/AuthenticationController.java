package dev.cnpe.m6finalsc.application.web.controller.mvc;

import dev.cnpe.m6finalsc.application.dto.auth.AuthenticationRequest;
import dev.cnpe.m6finalsc.application.dto.auth.RegistrationRequest;
import dev.cnpe.m6finalsc.application.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationRequest", RegistrationRequest.empty());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegistrationRequest request, HttpServletResponse response) {
        authenticationService.registerWeb(response, request);
        return "redirect:/students";
    }
}
