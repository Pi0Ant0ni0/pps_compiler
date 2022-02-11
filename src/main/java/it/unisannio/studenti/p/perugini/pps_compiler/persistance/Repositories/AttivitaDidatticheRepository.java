package it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories;

import it.unisannio.studenti.p.perugini.pps_compiler.persistance.entity.AttivitaDidatticaEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AttivitaDidatticheRepository extends MongoRepository<AttivitaDidatticaEntity, String> {
    @Query("{'$or' : [" +
                        "{ 'codiceCorsoDiStudio' : ?0}," +
                        "{ 'codiceCorsoDiStudio' : { '$ne' : ?0 }, 'nonErogabile': false }" +
            "] }")
    List<AttivitaDidatticaEntity> getCorsiCompatibiliConSceltaLibera(String codiceCorsoDiStudio);


}
