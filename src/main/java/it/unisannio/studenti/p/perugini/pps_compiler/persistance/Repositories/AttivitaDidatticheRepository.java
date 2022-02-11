package it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories;

import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AttivitaDidatticheRepository extends MongoRepository<AttivitaDidattica, String> {
    @Query("{'$or' : [" +
                        "{ 'codiceCorsoDiStudio' : ?0}," +
                        "{ 'codiceCorsoDiStudio' : { '$ne' : ?0 }, 'nonErogabile': false }" +
            "] }")
    List<AttivitaDidattica> getCorsiCompatibiliConSceltaLibera(String codiceCorsoDiStudio);


}
