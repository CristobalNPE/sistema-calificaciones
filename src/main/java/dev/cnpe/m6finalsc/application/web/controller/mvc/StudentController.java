package dev.cnpe.m6finalsc.application.web.controller.mvc;

import dev.cnpe.m6finalsc.application.dto.student.StudentRequest;
import dev.cnpe.m6finalsc.application.dto.student.StudentResponse;
import dev.cnpe.m6finalsc.application.dto.student.StudentSearchCriteria;
import dev.cnpe.m6finalsc.application.service.StudentService;
import dev.cnpe.m6finalsc.application.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final SubjectService subjectService;

    @GetMapping
    public String searchStudents(@ModelAttribute StudentSearchCriteria searchCriteria,
                                 Model model,
                                 @PageableDefault(size = 10, sort = "id", direction = DESC) Pageable pageable) {

        Page<StudentResponse> students = studentService.searchWithCustomMatcher(searchCriteria, pageable);

        model.addAttribute("students", students);

        return "students/list";
    }


    @GetMapping("/new")
    public String newStudentForm(Model model) {
        StudentRequest emptyForm = StudentRequest.builder().rut("").name("").address("").build();

        model.addAttribute("student", emptyForm);
        model.addAttribute("isNewStudent", true);
        return "students/form";
    }

    @PostMapping
    public String createStudent(@Valid @ModelAttribute("student") StudentRequest form, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isNewStudent", true);
            return "students/form";
        }

        studentService.create(form);
        redirectAttributes.addFlashAttribute("message", "Estudiante registrado exitosamente");
        redirectAttributes.addFlashAttribute("type", "success");
        return "redirect:/students";
    }


    @GetMapping("/{id}")
    public String getStudentById(Model model, @PathVariable(name = "id") Long id) {

        StudentResponse student = studentService.findById(id);

        model.addAttribute("student", student);
        return "students/detail";
    }

    @GetMapping("/{id}/edit")
    public String editStudentForm(@PathVariable Long id, Model model) {
        StudentResponse student = studentService.findById(id);
        StudentRequest form = StudentRequest.builder()
                                            .rut(student.rut())
                                            .name(student.name())
                                            .address(student.address())
                                            .build();
        model.addAttribute("student", form);
        model.addAttribute("isNewStudent", false);
        model.addAttribute("studentId", id);
        return "students/form";
    }

    @PostMapping("/{id}")
    public String updateStudent(@PathVariable Long id,
                                @Valid @ModelAttribute("student") StudentRequest form,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("isNewStudent", false);
            model.addAttribute("studentId", id);
            return "students/form";
        }
        studentService.update(id, form);
        redirectAttributes.addFlashAttribute("message", "Estudiante actualizado exitosamente");
        redirectAttributes.addFlashAttribute("type", "success");
        return "redirect:/students";
    }

}
