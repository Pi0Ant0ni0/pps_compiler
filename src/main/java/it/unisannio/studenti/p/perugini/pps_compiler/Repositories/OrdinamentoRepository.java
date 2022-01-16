package it.unisannio.studenti.p.perugini.pps_compiler.Repositories;

import it.unisannio.studenti.p.perugini.pps_compiler.API.Ordinamento;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrdinamentoRepository extends MongoRepository<Ordinamento,Integer> {
}
