package it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port;

import it.unisannio.studenti.p.perugini.pps_compiler.API.PPS;
import it.unisannio.studenti.p.perugini.pps_compiler.API.Studente;

import java.util.Optional;

public interface ReadPPSPort {
    Optional<PPS>findPPSById(Studente studente);
}
