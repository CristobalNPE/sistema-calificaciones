package dev.cnpe.m6finalsc.application.mapper;

import dev.cnpe.m6finalsc.application.dto.student.StudentRequest;
import dev.cnpe.m6finalsc.application.dto.student.StudentResponse;
import dev.cnpe.m6finalsc.application.dto.student.StudentSearchCriteria;
import dev.cnpe.m6finalsc.application.dto.subject.SubjectResponse;
import dev.cnpe.m6finalsc.domain.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static dev.cnpe.m6finalsc.application.utils.RutUtils.*;

@Component
@RequiredArgsConstructor
public class StudentMapper {

    private final SubjectMapper subjectMapper;

    public Student toEntity(StudentRequest req) {
        Student student = new Student();
        student.setName(req.name());
        student.setRut(cleanRut(req.rut()));
        student.setAddress(req.address());

        return student;
    }


    public Student toEntity(StudentSearchCriteria searchCriteria) {
        Student student = new Student();
        student.setName(searchCriteria.name());
        student.setRut(searchCriteria.rut());
        student.setAddress(searchCriteria.address());

        return student;
    }


    public StudentResponse toResponse(Student entity) {

        Set<SubjectResponse> subjectsAsDto = entity.getSubjects().stream()
                                                   .map(subjectMapper::toResponse)
                                                   .collect(Collectors.toSet());

        return StudentResponse.builder()
                              .id(entity.getId())
                              .name(entity.getName())
                              .rut(formatRut(entity.getRut()))
                              .address(entity.getAddress())
                              .subjects(subjectsAsDto)
                              .build();
    }
}
