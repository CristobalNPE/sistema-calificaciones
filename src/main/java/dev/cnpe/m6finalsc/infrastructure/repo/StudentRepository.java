package dev.cnpe.m6finalsc.infrastructure.repo;

import dev.cnpe.m6finalsc.application.dto.student.StudentSummary;
import dev.cnpe.m6finalsc.domain.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface StudentRepository extends JpaRepository<Student, Long>, QueryByExampleExecutor<Student> {

    boolean existsByRut(String rut);


    @Query("""
            select new dev.cnpe.m6finalsc.application.dto.student.StudentSummary(
                s.id,
                s.rut,
                s.name
            )
            from Student s
            join s.subjects sub
            where sub.id = :subjectId
            """)
    Page<StudentSummary> findBySubjectId(Long subjectId, Pageable pageable);

}
