package it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.usecase;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ManifestoDegliStudi;

import java.util.List;

public interface VisualizzaManifestoUseCase {
    List<ManifestoDegliStudi>getManifesti(String codiceCorsoDiStudio);
}
