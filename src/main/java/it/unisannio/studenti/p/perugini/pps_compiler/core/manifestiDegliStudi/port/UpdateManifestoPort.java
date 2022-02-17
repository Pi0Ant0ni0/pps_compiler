package it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ChiaveManifestoDegliStudi;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ManifestoDegliStudi;

public interface UpdateManifestoPort {
    void update(ChiaveManifestoDegliStudi chiaveManifestoDegliStudi,ManifestoDegliStudi manifestoDegliStudi);
}
