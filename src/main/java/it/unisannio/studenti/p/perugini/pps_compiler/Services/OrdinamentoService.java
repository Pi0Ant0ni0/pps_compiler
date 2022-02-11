package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.Ordinamento;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.OrdinamentoNonCorrettoException;
import it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.port.CreateOrdinamentoPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.port.ReadOrdinamentoPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.usecase.AggiungiOrdinamentoUseCase;
import it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.usecase.OrdinamentoCorrenteUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrdinamentoService  implements AggiungiOrdinamentoUseCase, OrdinamentoCorrenteUseCase {
    @Autowired
    private CreateOrdinamentoPort createOrdinamentoPort;
    @Autowired
    private ReadOrdinamentoPort readOrdinamentoPort;

    @Override
    public void aggiungiOrdinamento(Ordinamento ordinamento) throws OrdinamentoNonCorrettoException {
        this.createOrdinamentoPort.save(ordinamento);

    }

    @Override
    public Optional<Ordinamento> ordinamentoCorrente(String codice) {
        return this.readOrdinamentoPort.findOrdinamentoCorrente(codice);
    }
}
