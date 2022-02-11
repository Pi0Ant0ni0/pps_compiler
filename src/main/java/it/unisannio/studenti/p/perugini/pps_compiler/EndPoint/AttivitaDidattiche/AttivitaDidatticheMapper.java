package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.CorsoDiStudioNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.InsegnamentoNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.AttivitaDidatticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AttivitaDidatticheMapper {

    @Autowired
    AttivitaDidatticaService attivitaDidatticaService;

    public AttivitaDidatticaPPSDTO fromInsegnamentoToInsegnamentoDTO(AttivitaDidattica attivitaDidattica) {
        try {
            AttivitaDidatticaPPSDTO dto = new AttivitaDidatticaPPSDTO();
            CorsoDiStudio corsoDiStudio = this.attivitaDidatticaService.getCorsoDiStudioByInsegnamento(attivitaDidattica);
            dto.setCfu(attivitaDidattica.getCfu());
            dto.setCodiceAttivitaDidattica(attivitaDidattica.getCodiceAttivitaDidattica());
            dto.setDenominazioneAttivitaDidattica(attivitaDidattica.getDenominazioneAttivitaDidattica());
            dto.setCodiceCorsoDiStudio(corsoDiStudio.getCodice());
            dto.setDenominazioneCorsoDiStudio(corsoDiStudio.getDenominazione());
            dto.setSettoreScientificoDisciplinare(attivitaDidattica.getSettoreScientificoDisciplinare());
            return dto;
        } catch (CorsoDiStudioNotFoundException e) {
            return null;
        }
    }




    public AttivitaDidattica fromInsegnamentoRegolaToInsegnamento(InsegnamentoRegola insegnamentoRegola){
        try {
            return this.attivitaDidatticaService.getAttivitaDidatticaByID(insegnamentoRegola.getCodiceInsegnamento());
        } catch (InsegnamentoNotFoundException e) {
            return null;
        }
    }



    public AttivitaDidattica fromInsegnamentoPPSDTOToInsegnamento(AttivitaDidatticaPPSDTO attivitaDidatticaPPSDTO){
        try {
            return this.attivitaDidatticaService.getAttivitaDidatticaByID(attivitaDidatticaPPSDTO.getCodiceAttivitaDidattica());
        } catch (InsegnamentoNotFoundException e) {
            return null;
        }
    }



}
