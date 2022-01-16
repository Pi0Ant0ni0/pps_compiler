package it.unisannio.studenti.p.perugini.pps_compiler.Repositories;

import it.unisannio.studenti.p.perugini.pps_compiler.API.PPS;
import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PPSRepository extends MongoRepository<PPS, User> {

    @Query("{'rifiutato' : false, 'approvato' : false}")
    List<PPS> getAllPPsNonApprovatiENonRfiutati();
}
