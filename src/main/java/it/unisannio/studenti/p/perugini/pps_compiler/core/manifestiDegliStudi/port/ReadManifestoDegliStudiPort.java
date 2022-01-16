package it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ChiaveManifestoDegliStudi;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ManifestoDegliStudi;

import java.util.Optional;

public interface ReadManifestoDegliStudiPort {
    Optional<ManifestoDegliStudi> findManifestoById(ChiaveManifestoDegliStudi id);
}
