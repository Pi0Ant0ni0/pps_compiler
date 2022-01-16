package it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port;

import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.PPSNonValidoException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.PPSNotFoundException;

public interface UpdatePPSPort {
    void accettaPPS(User user) throws PPSNotFoundException, PPSNonValidoException;
    void rifiutaPPS(User user) throws PPSNotFoundException, PPSNonValidoException;
}
