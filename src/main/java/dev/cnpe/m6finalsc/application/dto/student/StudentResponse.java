package dev.cnpe.m6finalsc.application.dto.student;

import dev.cnpe.m6finalsc.application.dto.subject.SubjectResponse;
import lombok.Builder;

import java.util.Set;

@Builder
public record StudentResponse(
        Long id,
        String rut,
        String name,
        String address,
        Set<SubjectResponse> subjects
) {
}
