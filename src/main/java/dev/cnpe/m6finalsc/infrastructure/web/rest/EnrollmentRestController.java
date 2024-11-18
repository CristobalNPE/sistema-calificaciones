package dev.cnpe.m6finalsc.infrastructure.web.rest;

import dev.cnpe.m6finalsc.application.dto.enrollment.EnrollmentResult;
import dev.cnpe.m6finalsc.application.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollment")
@RequiredArgsConstructor
public class EnrollmentRestController {

    private final EnrollmentService enrollmentService;

    @PutMapping("/{studentId}")
    public EnrollmentResult enrollStudentInSubjects(@PathVariable Long studentId,
                                                    @RequestBody List<Long> targetSubjectIds) {
        return enrollmentService.enrollStudentToSubjects(studentId, targetSubjectIds);
    }

}
