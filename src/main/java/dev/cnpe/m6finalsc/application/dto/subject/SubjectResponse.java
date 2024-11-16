package dev.cnpe.m6finalsc.application.dto.subject;

import lombok.Builder;

@Builder
public record SubjectResponse(
        Long id,
        String name,
        Integer registeredStudents
) {
}
