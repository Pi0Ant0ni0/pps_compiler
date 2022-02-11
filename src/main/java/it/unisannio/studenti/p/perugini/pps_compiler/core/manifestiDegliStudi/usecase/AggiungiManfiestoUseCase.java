package it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.usecase;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ManifestoDegliStudi;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.OrdinamentoNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.ManifestoDegliStudiNonValidoException;

public interface AggiungiManfiestoUseCase {
    void addManifesto(ManifestoDegliStudi manifestoDegliStudi) throws ManifestoDegliStudiNonValidoException, OrdinamentoNotFoundException;
}
