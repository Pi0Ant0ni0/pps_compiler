package it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface ReadCorsoDiStudioPort {
    Optional<CorsoDiStudio> findCorsoDiStudioById(String codice);
}
