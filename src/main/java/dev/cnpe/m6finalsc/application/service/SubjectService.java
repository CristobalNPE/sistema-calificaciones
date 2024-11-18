package dev.cnpe.m6finalsc.application.service;

import dev.cnpe.m6finalsc.application.dto.student.StudentSummary;
import dev.cnpe.m6finalsc.application.dto.subject.SubjectRequest;
import dev.cnpe.m6finalsc.application.dto.subject.SubjectResponse;
import dev.cnpe.m6finalsc.application.mapper.SubjectMapper;
import dev.cnpe.m6finalsc.domain.DomainException;
import dev.cnpe.m6finalsc.domain.model.Subject;
import dev.cnpe.m6finalsc.infrastructure.repo.StudentRepository;
import dev.cnpe.m6finalsc.infrastructure.repo.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static dev.cnpe.m6finalsc.application.utils.RutUtils.formatRut;
import static dev.cnpe.m6finalsc.domain.DomainException.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final SubjectMapper subjectMapper;

    public List<SubjectResponse> findRegisteredSubjects() {
        return subjectRepository.findAll().stream()
                                .map(subjectMapper::toResponse)
                                .toList();
    }


    public Subject createSubject(SubjectRequest subjectRequest) {
        return subjectRepository.save(subjectMapper.toEntity(subjectRequest));
    }

    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }

    public SubjectResponse findById(Long id) {

        return subjectRepository.findById(id)
                                .map(subjectMapper::toResponse)
                                .orElseThrow(() -> new DomainException(RESOURCE_NOT_FOUND, id.toString()));
    }


    public Page<StudentSummary> findStudentsForSubject(Long subjectId, Pageable pageable) {

        if (!subjectRepository.existsById(subjectId)) {
            throw new DomainException(RESOURCE_NOT_FOUND, subjectId.toString());
        }

        return studentRepository.findBySubjectId(subjectId, pageable)
                                .map(ss -> new StudentSummary(ss.id(), formatRut(ss.rut()), ss.name()));
    }
}
