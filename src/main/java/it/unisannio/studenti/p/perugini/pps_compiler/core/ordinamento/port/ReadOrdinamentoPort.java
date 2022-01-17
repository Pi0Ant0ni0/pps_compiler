package it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.port;

import it.unisannio.studenti.p.perugini.pps_compiler.API.Ordinamento;

import java.util.Optional;

public interface ReadOrdinamentoPort {
    Optional<Ordinamento> findOrdinamentoCorrente();
}
