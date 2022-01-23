package it.unisannio.studenti.p.perugini.pps_compiler.core.pps.usecase;

import it.unisannio.studenti.p.perugini.pps_compiler.API.PPS;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.RichiestaNonValidaException;

import java.util.Optional;

public interface VisualizzaStatoPPSUseCase {
    Optional<PPS> getPPS(Email emailRichiedente, Email emailPPS) throws RichiestaNonValidaException;
}
