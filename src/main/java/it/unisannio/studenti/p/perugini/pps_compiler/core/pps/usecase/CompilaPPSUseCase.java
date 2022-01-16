package it.unisannio.studenti.p.perugini.pps_compiler.core.pps.usecase;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.PPS.PPSAggiuntaDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.InsegnamentoNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.PPSNonValidoException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.RegolaNotFoundException;

public interface CompilaPPSUseCase {
    void compila(PPSAggiuntaDTO pps, Email email) throws InsegnamentoNotFoundException, PPSNonValidoException, RegolaNotFoundException;
}
