package dev.cnpe.m6finalsc.application;

import com.github.javafaker.Faker;
import dev.cnpe.m6finalsc.application.service.EnrollmentService;
import dev.cnpe.m6finalsc.domain.model.Student;
import dev.cnpe.m6finalsc.domain.model.Subject;
import dev.cnpe.m6finalsc.domain.model.User;
import dev.cnpe.m6finalsc.domain.repo.StudentRepository;
import dev.cnpe.m6finalsc.domain.repo.SubjectRepository;
import dev.cnpe.m6finalsc.domain.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class SampleDataLoader implements CommandLineRunner {


    public static final int FAKE_STUDENTS_AMOUNT = 300;
    public static final int MAX_SUBJECTS_ENROLLED_PER_STUDENT = 5;

    private final StudentRepository studentRepo;
    private final SubjectRepository subjectRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final EnrollmentService enrollmentService;
    private final Random random = new Random();
    private final Faker faker;

    @Override
    public void run(String... args) throws Exception {

        long start = System.currentTimeMillis();
        seedData();
        long end = System.currentTimeMillis();

        log.info("🎉🎉🎉🎉🎉 Datos de muestra cargados en {} segundos 🎉🎉🎉🎉🎉", (end - start) / 1000.0);

    }

    private void seedData() {
        loadExampleSubjects();
        loadStudentsDumyData(FAKE_STUDENTS_AMOUNT);

        List<Student> savedStudents = studentRepo.findAll();
        List<Subject> savedSubjects = subjectRepo.findAll();

        enrollStudentsInRandomSubjects(savedStudents, savedSubjects);

        registerTestAdmin();
    }

    private void loadExampleSubjects() {
        List<String> exampleSubjectsNames = List.of(
                "Cálculo Diferencial",
                "Álgebra Lineal",
                "Física General",
                "Programación II",
                "Estructuras de Datos",
                "Desarrollo Web"
        );

        List<Subject> exampleSubjects = exampleSubjectsNames.stream()
                                                            .map(name -> new Subject(null, name, new HashSet<>()))
                                                            .toList();

        subjectRepo.saveAll(exampleSubjects);
        log.info("✅ {} Asignaturas registradas", exampleSubjects.size());

    }

    private void registerTestAdmin() {
        userRepo.save(User.builder()
                          .username("admin")
                          .password(passwordEncoder.encode("admin"))
                          .name("Administrador Prueba")
                          .email("admin@admin.com")
                          .role(User.Role.ADMIN)
                          .build());
        log.info("✅ Administrador de prueba registrado con credenciales: admin/admin");
    }

    private void loadStudentsDumyData(int amount) {
        List<Student> studentsDummyData = IntStream.rangeClosed(1, amount).mapToObj(i ->
                new Student(
                        null,
                        faker.regexify("[1-9]{1}[0-9]{7}[0-9kK]{1}"),
                        faker.name().fullName(),
                        faker.address().fullAddress(),
                        new HashSet<>())).toList();


        studentRepo.saveAll(studentsDummyData);
        log.info("✅ {} Estudiantes registrados", studentsDummyData.size());

    }

    private void enrollStudentsInRandomSubjects(List<Student> savedStudents, List<Subject> savedSubjects) {
        savedStudents.forEach(student -> {
            int numSubjects = random.nextInt(MAX_SUBJECTS_ENROLLED_PER_STUDENT) + 1;

            List<Long> randomSubjectIds = new ArrayList<>();
            List<Subject> subjectPool = new ArrayList<>(savedSubjects);

            for (int i = 0; i < numSubjects && !subjectPool.isEmpty(); i++) {
                int randomIndex = random.nextInt(subjectPool.size());
                randomSubjectIds.add(subjectPool.remove(randomIndex).getId());
            }

            enrollmentService.enrollStudentToSubjects(student.getId(), randomSubjectIds);
        });
        log.info("✅ Estudiantes inscritos en asignaturas aleatorias");

    }


}