package it.unisannio.studenti.p.perugini.pps_compiler.persistance.adapters;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ChiaveOrdinamento;
import it.unisannio.studenti.p.perugini.pps_compiler.API.Ordinamento;
import it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories.OrdinamentoRepository;
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
    public Optional<Ordinamento> findOrdinamentoCorrente(String codice) {
        return this.ordinamentoRepository.findAll(Sort.by("chiaveOrdinamento.annoDiRedazione")
                .descending())
                .stream()
                .filter(ordinamento -> ordinamento.getChiaveOrdinamento().getCodiceCorsoDiStudio().equals(codice))
                .findFirst();
    }

    @Override
    public Optional<Ordinamento> findOrdinamentoById(ChiaveOrdinamento chiaveOrdinamento) {
        return  this.ordinamentoRepository.findById(chiaveOrdinamento);
    }
}
