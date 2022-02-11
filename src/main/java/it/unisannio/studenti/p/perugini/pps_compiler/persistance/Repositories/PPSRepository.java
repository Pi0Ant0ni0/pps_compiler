package it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories;

import it.unisannio.studenti.p.perugini.pps_compiler.API.Studente;
import it.unisannio.studenti.p.perugini.pps_compiler.persistance.entity.PPSEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PPSRepository extends MongoRepository<PPSEntity, Studente> {

}
