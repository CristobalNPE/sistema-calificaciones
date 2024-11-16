package dev.cnpe.m6finalsc.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rut", nullable = false, unique = true)
    private String rut;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "adress")
    private String address;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "students_subjects",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> subjects = new HashSet<>();


    public void addSubject(Subject subject) {
        subjects.add(subject);
    }

    public boolean isEnrolledIn(Subject subject) {
        return subjects.contains(subject);
    }

    public Student removeSubject(Long subjectId) {
        subjects.removeIf(subject -> subject.getId().equals(subjectId));
        return this;
    }


}
