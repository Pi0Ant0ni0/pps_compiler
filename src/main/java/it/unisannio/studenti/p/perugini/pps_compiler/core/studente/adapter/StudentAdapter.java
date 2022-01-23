package it.unisannio.studenti.p.perugini.pps_compiler.core.studente.adapter;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.StudentDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.StudentDTORepository;
import it.unisannio.studenti.p.perugini.pps_compiler.core.studente.port.CreateStudentRegistrationPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.studente.port.ReadStudentPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentAdapter implements CreateStudentRegistrationPort, ReadStudentPort {
    @Autowired
    private StudentDTORepository studentDTORepository;

    @Override
    public void addUserNotVerified(StudentDTO studentDTO) {
        this.studentDTORepository.save(studentDTO);

    }

    @Override
    public Optional<StudentDTO> findStudentById(Email email) {
        return studentDTORepository.findById(email.getEmail());
    }
}
