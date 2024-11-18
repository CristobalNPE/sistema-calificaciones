package dev.cnpe.m6finalsc.application.service;

import dev.cnpe.m6finalsc.application.dto.student.StudentRequest;
import dev.cnpe.m6finalsc.application.dto.student.StudentResponse;
import dev.cnpe.m6finalsc.application.dto.student.StudentSearchCriteria;
import dev.cnpe.m6finalsc.application.mapper.StudentMapper;
import dev.cnpe.m6finalsc.domain.DomainException;
import dev.cnpe.m6finalsc.domain.model.Student;
import dev.cnpe.m6finalsc.infrastructure.repo.StudentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static dev.cnpe.m6finalsc.application.utils.RutUtils.cleanRut;
import static dev.cnpe.m6finalsc.domain.DomainException.ErrorCode.DUPLICATED_DATA;
import static dev.cnpe.m6finalsc.domain.DomainException.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {


    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public Page<StudentResponse> searchWithCustomMatcher(StudentSearchCriteria searchCriteria, Pageable pageable) {

        String cleanedRut = cleanRut(searchCriteria.rut());

        Student student = studentMapper.toEntity(
                new StudentSearchCriteria(cleanedRut, searchCriteria.name(), searchCriteria.address()));

        ExampleMatcher matcher = ExampleMatcher.matching()
                                               .withIgnoreCase()
                                               .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                                               .withIgnoreNullValues();

        Example<Student> studentExample = Example.of(student, matcher);

        return studentRepository.findAll(studentExample, pageable).map(studentMapper::toResponse);

    }

    public Page<StudentResponse> findAll(Pageable pageable) {

        Page<Student> students = studentRepository.findAll(pageable);

        return students.map(studentMapper::toResponse);
    }

    public Student create(StudentRequest studentReq) {

        if (studentRepository.existsByRut(studentReq.rut())) {
            throw new DomainException(DUPLICATED_DATA);
        }

        return studentRepository.save(studentMapper.toEntity(studentReq));
    }

    public StudentResponse findById(Long id) {
        return studentRepository.findById(id)
                                .map(studentMapper::toResponse)
                                .orElseThrow(() -> new DomainException(RESOURCE_NOT_FOUND, id.toString()));
    }


    public void update(Long id, @Valid StudentRequest form) {

        Student student = studentRepository.findById(id)
                                           .orElseThrow(() -> new DomainException(RESOURCE_NOT_FOUND, id.toString()));

        updateEntityFromRequest(student, form);

        studentRepository.save(student);

    }

    private void updateEntityFromRequest(Student student, StudentRequest form) {
        Optional.ofNullable(form.rut()).ifPresent(student::setRut);
        Optional.ofNullable(form.name()).ifPresent(student::setName);
        Optional.ofNullable(form.address()).ifPresent(student::setAddress);
    }
}
