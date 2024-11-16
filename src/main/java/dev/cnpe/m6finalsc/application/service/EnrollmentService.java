package dev.cnpe.m6finalsc.application.service;

import dev.cnpe.m6finalsc.application.dto.enrollment.EnrollmentResult;
import dev.cnpe.m6finalsc.application.dto.subject.SubjectSummary;
import dev.cnpe.m6finalsc.domain.DomainException;
import dev.cnpe.m6finalsc.domain.model.Student;
import dev.cnpe.m6finalsc.domain.model.Subject;
import dev.cnpe.m6finalsc.domain.repo.StudentRepository;
import dev.cnpe.m6finalsc.domain.repo.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static dev.cnpe.m6finalsc.application.dto.enrollment.EnrollmentResult.Status;
import static dev.cnpe.m6finalsc.domain.DomainException.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentService {

    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;

    private static List<SubjectSummary> getInvalidSubjects(List<Long> subjectIds, List<Subject> subjectsToEnroll) {

        Set<Long> foundSubjectsIds = subjectsToEnroll.stream().map(Subject::getId).collect(Collectors.toSet());

        return subjectIds.stream()
                         .filter(id -> !foundSubjectsIds.contains(id))
                         .map(id -> new SubjectSummary(id, "ID de materia inválido"))
                         .toList();
    }

    private static void processEnrollment(Subject subject, Student student, Map<Status, List<SubjectSummary>> results) {
        SubjectSummary summary = new SubjectSummary(subject.getId(), subject.getName());
        try {
            if (student.isEnrolledIn(subject)) {
                results.computeIfAbsent(Status.ALREADY_ENROLLED, s -> new ArrayList<>()).add(summary);
            } else {
                student.addSubject(subject);
                results.computeIfAbsent(Status.ENROLLED, s -> new ArrayList<>()).add(summary);
            }
        } catch (Exception e) {
            results.computeIfAbsent(Status.NOT_ENROLLED, s -> new ArrayList<>()).add(summary);
        }
    }

    public EnrollmentResult enrollStudentToSubjects(Long studentId, List<Long> subjectIds) {

        Student student = studentRepository.findById(studentId)
                                           .orElseThrow(() -> new DomainException(RESOURCE_NOT_FOUND, studentId.toString()));


        List<Subject> subjectsToEnroll = subjectRepository.findAllById(subjectIds);

        List<SubjectSummary> invalidSubjects = getInvalidSubjects(subjectIds, subjectsToEnroll);


        Map<Status, List<SubjectSummary>> results = new EnumMap<>(Status.class);
        results.putIfAbsent(Status.NOT_ENROLLED, invalidSubjects);

        subjectsToEnroll.forEach(subject -> processEnrollment(subject, student, results));

        return EnrollmentResult.from(studentId, results);

    }

    public List<Long> findEnrolledSubjectIds(Long studentId) {
        Set<Subject> subjects = studentRepository.findById(studentId)
                                                 .map(Student::getSubjects)
                                                 .orElseThrow(() -> new DomainException(RESOURCE_NOT_FOUND, studentId.toString()));

        return subjects.stream().map(Subject::getId).toList();
    }

    public void dropStudentFromSubject(Long studentId, Long subjectId) {
        Student student = studentRepository.findById(studentId)
                                           .orElseThrow(() -> new DomainException(RESOURCE_NOT_FOUND, studentId.toString()));

        student.removeSubject(subjectId);
        studentRepository.save(student);
    }
}
