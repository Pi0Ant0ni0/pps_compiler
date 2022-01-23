package it.unisannio.studenti.p.perugini.pps_compiler.Repositories;

import it.unisannio.studenti.p.perugini.pps_compiler.API.PPS;
import it.unisannio.studenti.p.perugini.pps_compiler.API.Studente;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PPSRepository extends MongoRepository<PPS, Studente> {

}
