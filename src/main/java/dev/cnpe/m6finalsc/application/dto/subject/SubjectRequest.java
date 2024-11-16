package dev.cnpe.m6finalsc.application.dto.subject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubjectRequest(@NotBlank @Size(min = 3, max = 28) String name) {


    public static SubjectRequest forName(String name) {
        return new SubjectRequest(name);
    }

    public static SubjectRequest empty() {
        return new SubjectRequest("");
    }
}
