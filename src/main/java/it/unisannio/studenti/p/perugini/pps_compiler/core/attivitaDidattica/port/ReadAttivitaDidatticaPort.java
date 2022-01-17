package it.unisannio.studenti.p.perugini.pps_compiler.core.attivitaDidattica.port;

import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;

import java.util.Optional;

public interface ReadAttivitaDidatticaPort {
    Optional<AttivitaDidattica> findAttivitaById(String codice);
}
