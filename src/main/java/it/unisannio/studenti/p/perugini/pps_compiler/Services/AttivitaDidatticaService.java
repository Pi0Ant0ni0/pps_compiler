package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.CorsoDiStudioNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.constants.ERR_MESSAGES;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.InsegnamentoNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.core.attivitaDidattica.port.ListAttivitaDidattichePort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.attivitaDidattica.port.ReadAttivitaDidatticaPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.ReadCorsoDiStudioPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttivitaDidatticaService {

    @Autowired
    private ListAttivitaDidattichePort listAttivitaDidattichePort;
    @Autowired
    private ReadAttivitaDidatticaPort readAttivitaDidatticaPort;
    @Autowired
    private ReadCorsoDiStudioPort readCorsoDiStudioPort;


    public List<AttivitaDidattica> getAttivitaDidatticaPerCorsoDiStudio(String corsoDiStudio){
        return this.listAttivitaDidattichePort.listAttivitaDidattiche()
                .stream()
                .filter(insegnamento -> insegnamento.getCorsoDiStudio().getCodice().equals(corsoDiStudio) && !insegnamento.isProgrammato())
                .collect(Collectors.toList());
    }

    public AttivitaDidattica getAttivitaDidatticaByID(String codiceInsegnamento) throws InsegnamentoNotFoundException {
        Optional<AttivitaDidattica> attivitaDidattica = this.readAttivitaDidatticaPort.findAttivitaById(codiceInsegnamento);
        if (attivitaDidattica.isPresent())
            return attivitaDidattica.get();
        throw new InsegnamentoNotFoundException(ERR_MESSAGES.ATTIVITA_NOT_FOUND);
    }

    public List<AttivitaDidattica> getAttivitaDidattiche() {
        return this.listAttivitaDidattichePort.listAttivitaDidattiche();
    }

    public List<AttivitaDidattica> getAttivitaDidatticheProgrammatePerCorsoDiStudio(String codiceCorsoDiStudio) {
        return this.listAttivitaDidattichePort.listAttivitaDidattiche()
                .stream()
                .filter(insegnamento -> insegnamento.getCorsoDiStudio().getCodice().equals(codiceCorsoDiStudio) && insegnamento.isProgrammato())
                .collect(Collectors.toList());
    }

    public List<AttivitaDidattica> getAttivitaDidattichePerDipartimento(String dipartimento){
        return this.listAttivitaDidattichePort.listAttivitaDidattiche()
                .stream()
                .filter(attivitaDidattica ->readCorsoDiStudioPort.findCorsoDiStudioById(attivitaDidattica.getCorsoDiStudio().getCodice()).get().getDenominazioneFacolta().equals(dipartimento.toUpperCase()))
                .collect(Collectors.toList());

    }
}
