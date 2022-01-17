package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.CorsoDiStudioNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.InsegnamentoNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.CorsiDiStudioRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.AttivitaDidatticheRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.ManifestiDegliStudiRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.core.attivitaDidattica.port.ListAttivitaDidattichePort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.attivitaDidattica.port.ReadAttivitaDidatticaPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.ReadCorsoDiStudioPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InsegnamentoService {

    @Autowired
    private ListAttivitaDidattichePort listAttivitaDidattichePort;
    @Autowired
    private ReadAttivitaDidatticaPort readAttivitaDidatticaPort;
    @Autowired
    private ReadCorsoDiStudioPort readCorsoDiStudioPort;

    public CorsoDiStudio getCorsoDiStudioByInsegnamento(AttivitaDidattica attivitaDidattica) throws CorsoDiStudioNotFoundException {
        Optional<CorsoDiStudio> corsoDiStudioOptional = this.readCorsoDiStudioPort.findCorsoDiStudioById(attivitaDidattica.getCodiceCorsoDiStudio());
        if (corsoDiStudioOptional.isPresent())
            return corsoDiStudioOptional.get();
        throw new CorsoDiStudioNotFoundException("L'insegnamento con codice: "+ attivitaDidattica.getCodiceAttivitaDidattica()+" ha un codice corso di studio che non trova riscontro nel database locale");

    }

    public List<AttivitaDidattica> getInsegnamentiPerCorsoDiStudio(String corsoDiStudio){
        return this.listAttivitaDidattichePort.listAttivitaDidattiche()
                .stream()
                .filter(insegnamento -> insegnamento.getCodiceCorsoDiStudio().equals(corsoDiStudio) && !insegnamento.isProgrammato())
                .collect(Collectors.toList());
    }

    public AttivitaDidattica getInsegnamentoById(String codiceInsegnamento) throws InsegnamentoNotFoundException {
        Optional<AttivitaDidattica> insegnamento = this.readAttivitaDidatticaPort.findAttivitaById(codiceInsegnamento);
        if (insegnamento.isPresent())
            return insegnamento.get();
        throw new InsegnamentoNotFoundException("Insegnamento non presente nel DB");
    }

    public List<AttivitaDidattica> getInsegnamenti() {
        return this.listAttivitaDidattichePort.listAttivitaDidattiche();
    }

    public List<AttivitaDidattica> getInsegnamentiProgrammatiPerCorsoDiStudio(String codiceCorsoDiStudio) {
        return this.listAttivitaDidattichePort.listAttivitaDidattiche()
                .stream()
                .filter(insegnamento -> insegnamento.getCodiceCorsoDiStudio().equals(codiceCorsoDiStudio) && insegnamento.isProgrammato())
                .collect(Collectors.toList());
    }

    public List<AttivitaDidattica> getAttivitaDidattichePerDipartimento(String dipartimento){
        return this.listAttivitaDidattichePort.listAttivitaDidattiche()
                .stream()
                .filter(attivitaDidattica ->readCorsoDiStudioPort.findCorsoDiStudioById(attivitaDidattica.getCodiceCorsoDiStudio()).get().getDenominazioneFacolta().equals(dipartimento.toUpperCase()))
                .collect(Collectors.toList());

    }
}
