package dev.cnpe.m6finalsc.infrastructure.web.mvc;

import dev.cnpe.m6finalsc.application.dto.subject.SubjectRequest;
import dev.cnpe.m6finalsc.application.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public String getSubjects(Model model) {
        model.addAttribute("subjects", subjectService.findRegisteredSubjects());

        return "subjects/list";
    }

    @GetMapping("/{id}")
    public String getSubjectById(Model model,
                                 @PathVariable Long id,
                                 @PageableDefault(size = 10) Pageable pageable) {

        model.addAttribute("subject", subjectService.findById(id));
        model.addAttribute("registeredStudents", subjectService.findStudentsForSubject(id, pageable));

        return "subjects/detail";
    }

    @GetMapping("/new")
    public String newSubjectForm(Model model) {
        SubjectRequest emptyForm = SubjectRequest.empty();

        model.addAttribute("subject", emptyForm);
        model.addAttribute("isNewSubject", true);
        return "subjects/form";
    }

    @PostMapping
    public String createSubject(@Valid @ModelAttribute("subject") SubjectRequest form,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isNewSubject", true);
            return "subjects/form";
        }

        subjectService.createSubject(form);
        redirectAttributes.addFlashAttribute("message", "Asignatura registrada exitosamente");
        redirectAttributes.addFlashAttribute("type", "success");
        return "redirect:/subjects";
    }

    @PostMapping("/{id}/delete")
    public String deleteSubject(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {

        subjectService.deleteSubject(id);
        redirectAttributes.addFlashAttribute("message", "Asignatura eliminada exitosamente");
        redirectAttributes.addFlashAttribute("type", "success");
        return "redirect:/subjects";
    }


}
