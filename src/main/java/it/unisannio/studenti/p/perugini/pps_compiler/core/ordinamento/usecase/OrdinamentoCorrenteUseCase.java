package it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.usecase;

import it.unisannio.studenti.p.perugini.pps_compiler.API.Ordinamento;

import java.util.Optional;

public interface OrdinamentoCorrenteUseCase {
    Optional<Ordinamento> ordinamentoCorrente();
}
