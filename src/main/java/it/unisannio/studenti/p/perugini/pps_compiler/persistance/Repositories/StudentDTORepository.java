package it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories;

import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.StudentDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentDTORepository extends MongoRepository<StudentDTO,String> {
}
