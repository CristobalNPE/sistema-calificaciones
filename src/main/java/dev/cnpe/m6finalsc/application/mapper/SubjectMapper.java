package dev.cnpe.m6finalsc.application.mapper;

import dev.cnpe.m6finalsc.application.dto.subject.SubjectRequest;
import dev.cnpe.m6finalsc.application.dto.subject.SubjectResponse;
import dev.cnpe.m6finalsc.domain.model.Subject;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class SubjectMapper {

    public SubjectResponse toResponse(Subject entity) {
        return SubjectResponse.builder()
                              .id(entity.getId())
                              .name(entity.getName())
                              .registeredStudents(entity.getStudents().size())
                              .build();
    }

    public Subject toEntity(SubjectRequest subjectRequest) {
        return new Subject(null, subjectRequest.name(), new HashSet<>());
    }


}
