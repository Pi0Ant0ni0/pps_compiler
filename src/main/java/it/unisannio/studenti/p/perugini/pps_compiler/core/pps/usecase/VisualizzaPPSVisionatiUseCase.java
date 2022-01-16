package it.unisannio.studenti.p.perugini.pps_compiler.core.pps.usecase;

import it.unisannio.studenti.p.perugini.pps_compiler.API.PPS;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;

import java.util.List;

public interface VisualizzaPPSVisionatiUseCase {
    List<PPS> getPPSVisionati(Email email);
}
