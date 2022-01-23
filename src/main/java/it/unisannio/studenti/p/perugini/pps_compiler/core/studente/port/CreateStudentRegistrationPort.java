package it.unisannio.studenti.p.perugini.pps_compiler.core.studente.port;

import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.StudentDTO;

public interface CreateStudentRegistrationPort {
    void addUserNotVerified(StudentDTO studentDTO);
}
