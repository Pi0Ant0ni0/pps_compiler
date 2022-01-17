package it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.usecase;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ManifestoDegliStudi;

import java.util.Optional;

public interface ManifestoPDFUseCase {
    Optional<ManifestoDegliStudi> manifestoPDF(int coorte, String codiceCorsoDiStudio, String curricula);
}
