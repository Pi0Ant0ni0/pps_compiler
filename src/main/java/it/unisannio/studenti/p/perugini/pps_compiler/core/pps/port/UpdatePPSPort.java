package it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port;

import it.unisannio.studenti.p.perugini.pps_compiler.API.Studente;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.PPSNonValidoException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.PPSNotFoundException;

public interface UpdatePPSPort {
    void accettaPPS(Studente studente) throws PPSNotFoundException, PPSNonValidoException;
    void rifiutaPPS(Studente studente) throws PPSNotFoundException, PPSNonValidoException;
}
