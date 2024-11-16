package dev.cnpe.m6finalsc.application.dto.enrollment;

import dev.cnpe.m6finalsc.application.dto.subject.SubjectSummary;

import java.util.List;
import java.util.Map;

public record EnrollmentResult(
        EnrollmentSummary summary,
        Map<Status, List<SubjectSummary>> results
) {
    public static EnrollmentResult from(
            Long studentId,
            Map<Status, List<SubjectSummary>> results
    ) {
        var summary = new EnrollmentSummary(
                studentId,
                results.getOrDefault(Status.ENROLLED, List.of()).size(),
                results.getOrDefault(Status.ALREADY_ENROLLED, List.of()).size(),
                results.getOrDefault(Status.NOT_ENROLLED, List.of()).size()
        );
        return new EnrollmentResult(summary, results);
    }

    public enum Status {
        ALREADY_ENROLLED,
        ENROLLED,
        NOT_ENROLLED,
    }

    public record EnrollmentSummary(
            Long studentId,
            int totalEnrolled,
            int alreadyEnrolled,
            int failedEnrollments
    ) {
    }


}


