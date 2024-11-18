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

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class SampleDataLoader implements CommandLineRunner {


    public static final int FAKE_STUDENTS_AMOUNT = 1000;
    public static final int MAX_SUBJECTS_ENROLLED_PER_STUDENT = 5;
    private static final List<String> EXAMPLE_SUBJECTS = List.of(
            "Cálculo Diferencial",
            "Álgebra Lineal",
            "Física General",
            "Programación II",
            "Estructuras de Datos",
            "Desarrollo Web"
    );
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    private final StudentRepository studentRepo;
    private final SubjectRepository subjectRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final EnrollmentService enrollmentService;
    private final Random random = new Random();
    private final Faker faker;

    @Override
    public void run(String... args) {
        try {
            long start = System.currentTimeMillis();
            seedData();
            logExecutionTime(start);
        } catch (Exception e) {
            log.error("Error al iniciar cargar datos de muestra: ", e);
            throw new RuntimeException("Error al iniciar cargar datos de muestra: ", e);
        }
    }

    private void seedData() {
        List<Subject> subjects = loadExampleSubjects();
        List<Student> students = loadStudentsDummyData(FAKE_STUDENTS_AMOUNT);
        enrollStudentsInRandomSubjects(students, subjects);
        registerTestAdmin();
    }

    private List<Subject> loadExampleSubjects() {

        List<Subject> subjects = EXAMPLE_SUBJECTS.stream()
                                                 .map(name -> new Subject(null, name, new HashSet<>()))
                                                 .toList();

        List<Subject> savedSubjects = subjectRepo.saveAll(subjects);
        log.info("✅ {} Asignaturas registradas", savedSubjects.size());

        return savedSubjects;
    }

    private void registerTestAdmin() {
        userRepo.save(User.builder()
                          .username(ADMIN_USERNAME)
                          .password(passwordEncoder.encode(ADMIN_PASSWORD))
                          .name("Administrador Prueba")
                          .email("admin@admin.com")
                          .role(User.Role.ADMIN)
                          .build());
        log.info("✅ Administrador de prueba registrado con credenciales: {}/{}", ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    private List<Student> loadStudentsDummyData(int amount) {
        List<Student> studentsDummyData = IntStream.range(0, amount).mapToObj(i ->
                new Student(
                        null,
                        generateValidRut(),
                        faker.name().fullName(),
                        faker.address().fullAddress(),
                        new HashSet<>())).toList();


        List<Student> savedStudents = studentRepo.saveAll(studentsDummyData);
        log.info("✅ {} Estudiantes registrados", savedStudents.size());

        return savedStudents;

    }

    private void enrollStudentsInRandomSubjects(List<Student> students, List<Subject> subjects) {
        students.parallelStream().forEach(student -> {
            int numSubjects = random.nextInt(MAX_SUBJECTS_ENROLLED_PER_STUDENT) + 1;
            List<Long> randomSubjectIds = getRandomSubjectIds(subjects, numSubjects);
            enrollmentService.enrollStudentToSubjects(student.getId(), randomSubjectIds);
        });
        log.info("✅ Estudiantes inscritos en asignaturas aleatorias");
    }

    private List<Long> getRandomSubjectIds(List<Subject> subjects, int count) {
        List<Subject> subjectsCopy = new ArrayList<>(subjects);

        Collections.shuffle(subjectsCopy, random);
        return subjectsCopy.stream()
                           .limit(count)
                           .map(Subject::getId)
                           .toList();

    }

    private void logExecutionTime(long start) {
        double executionTime = (System.currentTimeMillis() - start) / 1000.0;
        log.info("🎉🎉🎉🎉🎉 Datos de muestra cargados en {} segundos 🎉🎉🎉🎉🎉", executionTime);
    }

    private String generateValidRut() {
        return faker.regexify("[1-9]{1}[0-9]{7}[0-9kK]{1}");
    }


}