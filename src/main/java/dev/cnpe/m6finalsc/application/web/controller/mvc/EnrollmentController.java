package dev.cnpe.m6finalsc.application.web.controller.mvc;

import dev.cnpe.m6finalsc.application.dto.enrollment.EnrollmentResult;
import dev.cnpe.m6finalsc.application.dto.subject.SubjectResponse;
import dev.cnpe.m6finalsc.application.service.EnrollmentService;
import dev.cnpe.m6finalsc.application.service.SubjectService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {


    private final EnrollmentService enrollmentService;
    private final SubjectService subjectService;

    @GetMapping("/{studentId}")
    public String enrollStudentInSubjects(@PathVariable Long studentId, Model model) {
        List<SubjectResponse> registeredSubjects = subjectService.findRegisteredSubjects();
        List<Long> enrolledSubjectIds = enrollmentService.findEnrolledSubjectIds(studentId);

        List<SubjectResponse> availableSubjects = registeredSubjects.stream()
                                                                    .filter(subject -> !enrolledSubjectIds.contains(subject.id()))
                                                                    .toList();

        model.addAttribute("subjects", availableSubjects);
        model.addAttribute("studentId", studentId);

        return "students/enrollment";
    }

    @PostMapping("/{studentId}")
    public String enrollStudentInSubjects(@PathVariable Long studentId,
                                          @RequestParam(name = "targetSubjectIds", required = false) List<Long> targetSubjectIds,
                                          RedirectAttributes redirectAttributes) {

        EnrollmentResult result = enrollmentService.enrollStudentToSubjects(studentId, targetSubjectIds);
        redirectAttributes.addFlashAttribute("message", "Inscripción realizada con éxito");
        redirectAttributes.addFlashAttribute("type", "success");
        return "redirect:/students/" + studentId;
    }

    @PostMapping("/{studentId}/{subjectId}")
    public String dropStudentFromSubject(@PathVariable Long studentId,
                                         @PathVariable Long subjectId,
                                         HttpServletRequest request,
                                         RedirectAttributes redirectAttributes) {

        enrollmentService.dropStudentFromSubject(studentId, subjectId);
        redirectAttributes.addFlashAttribute("message", "Inscripción eliminada con éxito");
        redirectAttributes.addFlashAttribute("type", "success");

        String referer = request.getHeader("Referer");

        return referer != null && referer.contains("/subjects")
                ? "redirect:/subjects/" + subjectId
                : "redirect:/students/" + studentId;
    }

}
