package it.unisannio.studenti.p.perugini.pps_compiler.core.student.port;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.StudentDTO;

import java.util.Optional;

public interface ReadStudentPort {
    Optional<StudentDTO> findStudentById(Email email);
}
