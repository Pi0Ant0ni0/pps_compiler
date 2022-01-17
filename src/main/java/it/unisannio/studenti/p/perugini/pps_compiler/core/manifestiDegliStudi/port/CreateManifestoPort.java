package it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ManifestoDegliStudi;

public interface CreateManifestoPort {
    void save(ManifestoDegliStudi manifestoDegliStudi);
}
