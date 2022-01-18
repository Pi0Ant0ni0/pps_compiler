package it.unisannio.studenti.p.perugini.pps_compiler.core.service;

import it.unisannio.studenti.p.perugini.pps_compiler.API.Ordinamento;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.Ordinamento.OrdinamentoMapper;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.OrdinamentoNonCorrettoException;
import it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.port.CreateOrdinamentoPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.port.ReadOrdinamentoPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.usecase.AggiungiOrdinamentoUseCase;
import it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.usecase.OrdinamentoCorrenteUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
        if (ordinamento.getCfuMinimiAScelta()+ordinamento.getCfuMinimiOrientamento() +ordinamento.getCfuMinimiObbligatori()!=ordinamento.getCfuMinimiCorsoDiLaurea())
            throw new OrdinamentoNonCorrettoException("la somma dei cfu minimi deve essere uguale alla quota di cfu minimi del corso di laurea");
        if (ordinamento.getCfuMassimiAScelta()+ordinamento.getCfuMassimiOrientamento() +ordinamento.getCfuMassimiObbligatori()!=ordinamento.getCfuMassimiCorsoDiLaurea())
            throw new OrdinamentoNonCorrettoException("la somma dei cfu massimi deve essere uguale alla quota di cfu massimi del corso di laurea");

        this.createOrdinamentoPort.save(ordinamento);

    }

    @Override
    public Optional<Ordinamento> ordinamentoCorrente(String codice) {
        return this.readOrdinamentoPort.findOrdinamentoCorrente(codice);
    }
}
