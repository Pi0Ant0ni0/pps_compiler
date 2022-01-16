package it.unisannio.studenti.p.perugini.pps_compiler.Repositories;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CorsiDiStudioRepository extends MongoRepository<CorsoDiStudio,String> {
}
