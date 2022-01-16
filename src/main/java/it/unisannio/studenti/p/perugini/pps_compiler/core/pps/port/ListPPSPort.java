package it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port;

import it.unisannio.studenti.p.perugini.pps_compiler.API.PPS;
import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;

import java.util.List;

public interface ListPPSPort {
    List<PPS>findPPSInSospesoByUser(User user);
    List<PPS>findPPSVisionatiByUser(User user);
}
