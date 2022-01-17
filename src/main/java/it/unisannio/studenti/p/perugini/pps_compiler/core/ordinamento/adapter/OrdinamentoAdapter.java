package it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.adapter;

import it.unisannio.studenti.p.perugini.pps_compiler.API.Ordinamento;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.OrdinamentoRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.port.CreateOrdinamentoPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.port.ReadOrdinamentoPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrdinamentoAdapter implements CreateOrdinamentoPort, ReadOrdinamentoPort {
    @Autowired
    private OrdinamentoRepository ordinamentoRepository;

    @Override
    public void save(Ordinamento ordinamento) {
        this.ordinamentoRepository.save(ordinamento);

    }

    @Override
    public Optional<Ordinamento> findOrdinamentoCorrente() {
        return this.ordinamentoRepository.findAll(Sort.by("annoDiRedazione").ascending()).stream().findFirst();
    }
}
