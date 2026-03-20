package dse.thesis404.application.port;

import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.student.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    Optional<Student> findById(ProfileId id);
    List<Student> findAll();
    void save(Student student);
    void delete(ProfileId id);
    boolean exists(ProfileId id);
}
