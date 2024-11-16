package dev.cnpe.m6finalsc.application.web.controller.rest;

import dev.cnpe.m6finalsc.application.dto.student.StudentRequest;
import dev.cnpe.m6finalsc.application.dto.student.StudentResponse;
import dev.cnpe.m6finalsc.application.dto.student.StudentSearchCriteria;
import dev.cnpe.m6finalsc.application.service.StudentService;
import dev.cnpe.m6finalsc.domain.model.Student;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentRestController {

    private final StudentService studentService;

    @GetMapping("/search")
    public Page<StudentResponse> search(@RequestParam(required = false) String rut,
                                        @RequestParam(required = false) String name,
                                        @RequestParam(required = false) String address,
                                        Pageable pageable) {


        return studentService.searchWithCustomMatcher(new StudentSearchCriteria(rut, name, address), pageable);
    }


    @GetMapping
    public Page<StudentResponse> findAll(Pageable pageable) {
        return studentService.findAll(pageable);
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid StudentRequest studentRequest,
                                     UriComponentsBuilder ucb) {
        Student student = studentService.create(studentRequest);
        URI uri = ucb.path("/{id}").buildAndExpand(student.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }


}
