package it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.usecase;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ChiaveManifestoDegliStudi;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ManifestoDegliStudi;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.ManifestoDegliStudiNonValidoException;

public interface UpdateManifestoUseCase {
    void update(ChiaveManifestoDegliStudi chiaveManifestoDegliStudi,ManifestoDegliStudi manifestoDegliStudi) throws ManifestoDegliStudiNonValidoException;
}
