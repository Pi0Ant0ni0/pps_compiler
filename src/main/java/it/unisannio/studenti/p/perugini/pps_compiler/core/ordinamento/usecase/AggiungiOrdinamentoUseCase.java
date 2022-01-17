package it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.usecase;

import it.unisannio.studenti.p.perugini.pps_compiler.API.Ordinamento;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.OrdinamentoNonCorrettoException;

public interface AggiungiOrdinamentoUseCase {
    void aggiungiOrdinamento(Ordinamento ordinamento) throws OrdinamentoNonCorrettoException;
}
