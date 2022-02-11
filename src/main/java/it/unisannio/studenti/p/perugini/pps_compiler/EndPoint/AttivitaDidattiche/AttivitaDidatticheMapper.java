package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche;

import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.InsegnamentoNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.AttivitaDidatticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AttivitaDidatticheMapper {

    @Autowired
    AttivitaDidatticaService attivitaDidatticaService;

    public AttivitaDidatticaPPSDTO toAttivitaDidatticaPPSDTO(AttivitaDidattica attivitaDidattica) {
        AttivitaDidatticaPPSDTO dto = new AttivitaDidatticaPPSDTO();
        dto.setCfu(attivitaDidattica.getCfu());
        dto.setCodiceAttivitaDidattica(attivitaDidattica.getCodiceAttivitaDidattica());
        dto.setDenominazioneAttivitaDidattica(attivitaDidattica.getDenominazioneAttivitaDidattica());
        dto.setCodiceCorsoDiStudio(attivitaDidattica.getCorsoDiStudio().getCodice());
        dto.setDenominazioneCorsoDiStudio(attivitaDidattica.getCorsoDiStudio().getDenominazione());
        dto.setSettoreScientificoDisciplinare(attivitaDidattica.getSettoreScientificoDisciplinare());
        return dto;

    }


    public AttivitaDidattica fromInsegnamentoRegolaToAttivitaDidattica(InsegnamentoRegola insegnamentoRegola) {
        try {
            return this.attivitaDidatticaService.getAttivitaDidatticaByID(insegnamentoRegola.getCodiceInsegnamento());
        } catch (InsegnamentoNotFoundException e) {
            return null;
        }
    }


    public AttivitaDidattica toAttivitaDidattica(AttivitaDidatticaPPSDTO attivitaDidatticaPPSDTO) {
        try {
            return this.attivitaDidatticaService.getAttivitaDidatticaByID(attivitaDidatticaPPSDTO.getCodiceAttivitaDidattica());
        } catch (InsegnamentoNotFoundException e) {
            return null;
        }
    }

    public AttivitaDidatticaDettagliata toAttivitaDidatticaDettagliata(AttivitaDidattica attivitaDidattica){
        return new AttivitaDidatticaDettagliata(
                attivitaDidattica.getCodiceAttivitaDidattica(),
                attivitaDidattica.getDenominazioneAttivitaDidattica(),
                attivitaDidattica.getCfu(),
                attivitaDidattica.getCorsoDiStudio().getCodice(),
                attivitaDidattica.getSettoreScientificoDisciplinare(),
                attivitaDidattica.getContenuti(),
                attivitaDidattica.getMetodiDidattici(),
                attivitaDidattica.getModalitaVerificaApprendimento(),
                attivitaDidattica.getObiettivi(),
                attivitaDidattica.getPrerequisiti()
        );
    }


}
