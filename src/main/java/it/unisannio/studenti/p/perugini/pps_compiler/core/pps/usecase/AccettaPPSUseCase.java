package it.unisannio.studenti.p.perugini.pps_compiler.core.pps.usecase;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.PPSNonValidoException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.PPSNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.UserNotFound;

public interface AccettaPPSUseCase {
    void accettaPPS(Email email) throws PPSNotFoundException, UserNotFound, PPSNonValidoException;
}
