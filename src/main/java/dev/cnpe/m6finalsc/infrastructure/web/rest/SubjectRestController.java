package dev.cnpe.m6finalsc.infrastructure.web.rest;

import dev.cnpe.m6finalsc.application.dto.student.StudentSummary;
import dev.cnpe.m6finalsc.application.dto.subject.SubjectRequest;
import dev.cnpe.m6finalsc.application.dto.subject.SubjectResponse;
import dev.cnpe.m6finalsc.application.service.SubjectService;
import dev.cnpe.m6finalsc.domain.model.Subject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectRestController {

    private final SubjectService subjectService;


    @GetMapping
    public List<SubjectResponse> getRegisteredSubjects() {

        return subjectService.findRegisteredSubjects();
    }

    @PostMapping
    public ResponseEntity<Void> registerSubject(@RequestBody @Valid SubjectRequest subjectRequest) {
        Subject subject = subjectService.createSubject(subjectRequest);
        URI location = fromCurrentRequest().path("/{id}")
                                           .buildAndExpand(subject.getId())
                                           .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public SubjectResponse getSubject(@PathVariable Long id) {
        return subjectService.findById(id);
    }

    @GetMapping("/{subjectId}/students")
    public Page<StudentSummary> getStudentsForSubject(@PathVariable Long subjectId, Pageable pageable) {
        return subjectService.findStudentsForSubject(subjectId, pageable);
    }

}
