package it.unisannio.studenti.p.perugini.pps_compiler.Repositories;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ChiaveManifestoDegliStudi;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ManifestoDegliStudi;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ManifestiDegliStudiRepository extends MongoRepository<ManifestoDegliStudi, ChiaveManifestoDegliStudi> {
}
