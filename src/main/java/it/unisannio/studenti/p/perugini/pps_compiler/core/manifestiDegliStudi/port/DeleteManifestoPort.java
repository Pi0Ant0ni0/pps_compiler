package it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ChiaveManifestoDegliStudi;

public interface DeleteManifestoPort {
    void delete(ChiaveManifestoDegliStudi chiaveManifestoDegliStudi);
}
