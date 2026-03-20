package dse.thesis404.infrastructure.persistence.jdbc;

import dse.thesis404.application.port.StudentRepository;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.student.Student;
import dse.thesis404.infrastructure.persistence.jdbc.entity.StudentAggregate;
import dse.thesis404.infrastructure.persistence.jdbc.entity.StudentFachgebiet;
import dse.thesis404.infrastructure.persistence.jdbc.entity.StudentVeranstaltung;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class JdbcStudentRepository implements StudentRepository {

    private final StudentDataRepository studentDataRepository;

    public JdbcStudentRepository(StudentDataRepository studentDataRepository) {
        this.studentDataRepository = studentDataRepository;
    }

    @Override
    public Optional<Student> findById(ProfileId id) {
        return studentDataRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<Student> findAll() {
        List<Student> result = new ArrayList<>();
        studentDataRepository.findAll().forEach(agg -> result.add(toDomain(agg)));
        return result;
    }

    @Override
    public void save(Student student) {
        boolean exists = studentDataRepository.existsById(student.getId().value());
        StudentAggregate agg = toAggregate(student);
        agg.setNew(!exists);
        if (exists) {
            studentDataRepository.findById(student.getId().value()).ifPresent(existing ->
                    agg.setCreatedAt(existing.getCreatedAt()));
        } else {
            agg.setCreatedAt(java.time.Instant.now());
        }
        studentDataRepository.save(agg);
    }

    @Override
    public void delete(ProfileId id) {
        studentDataRepository.deleteById(id.value());
    }

    @Override
    public boolean exists(ProfileId id) {
        return studentDataRepository.existsById(id.value());
    }

    private Student toDomain(StudentAggregate agg) {
        Student student = new Student(new ProfileId(agg.getProfileId()));
        student.setDisplayName(agg.getDisplayName());
        if (agg.getVeranstaltungen() != null) {
            agg.getVeranstaltungen().stream()
                    .map(StudentVeranstaltung::getVeranstaltung)
                    .filter(v -> v != null && !v.isBlank())
                    .forEach(student::addVeranstaltung);
        }
        if (agg.getFachgebiete() != null) {
            agg.getFachgebiete().stream()
                    .map(StudentFachgebiet::getFachgebiet)
                    .filter(f -> f != null && !f.isBlank())
                    .forEach(student::addInteresse);
        }
        return student;
    }

    private StudentAggregate toAggregate(Student student) {
        StudentAggregate agg = new StudentAggregate();
        agg.setProfileId(student.getId().value());
        agg.setDisplayName(student.getDisplayName());
        Set<StudentVeranstaltung> veranstaltungen = student.getBesuchteVeranstaltungen().stream()
                .map(StudentVeranstaltung::new)
                .collect(Collectors.toSet());
        agg.setVeranstaltungen(veranstaltungen);
        Set<StudentFachgebiet> fachgebiete = student.getInteressen().stream()
                .map(StudentFachgebiet::new)
                .collect(Collectors.toSet());
        agg.setFachgebiete(fachgebiete);
        return agg;
    }
}
