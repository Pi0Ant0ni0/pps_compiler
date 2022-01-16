package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.CorsoDiStudioNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.InsegnamentoNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.InsegnamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AttivitaDidatticheMapper {

    @Autowired
    InsegnamentoService insegnamentoService;

    public AttivitaDidatticaPPSDTO fromInsegnamentoToInsegnamentoDTO(AttivitaDidattica attivitaDidattica) {
        try {
            AttivitaDidatticaPPSDTO dto = new AttivitaDidatticaPPSDTO();
            CorsoDiStudio corsoDiStudio = this.insegnamentoService.getCorsoDiStudioByInsegnamento(attivitaDidattica);
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


    public AttivitaDidatticaDiOrientamentoDTO fromInsegnamentoRegolaToInsegnamentoOrientamento(InsegnamentoRegola insegnamentoRegola){
        AttivitaDidatticaDiOrientamentoDTO orientamento = new AttivitaDidatticaDiOrientamentoDTO();
        orientamento.setCodiceAttivitaDidattica(insegnamentoRegola.getCodiceInsegnamento());
        orientamento.setDenominazioneAttivitaDidattica(insegnamentoRegola.getDenominazioneInsegnamento());
        orientamento.setCfu(insegnamentoRegola.getCfu());
        return orientamento;
    }

    public AttivitaDidattica fromInsegnamentoRegolaToInsegnamento(InsegnamentoRegola insegnamentoRegola){
        try {
            return this.insegnamentoService.getInsegnamentoById(insegnamentoRegola.getCodiceInsegnamento());
        } catch (InsegnamentoNotFoundException e) {
            return null;
        }
    }

    public AttivitaDidattica fromInsegnamentoOrientamentoToInsegnamento(AttivitaDidatticaDiOrientamentoDTO attivitaDidatticaDiOrientamentoDTO){
        try {
            return this.insegnamentoService.getInsegnamentoById(attivitaDidatticaDiOrientamentoDTO.getCodiceAttivitaDidattica());
        } catch (InsegnamentoNotFoundException e) {
            return null;
        }
    }

    public AttivitaDidattica fromInsegnamentoPPSDTOToInsegnamento(AttivitaDidatticaPPSDTO attivitaDidatticaPPSDTO){
        try {
            return this.insegnamentoService.getInsegnamentoById(attivitaDidatticaPPSDTO.getCodiceAttivitaDidattica());
        } catch (InsegnamentoNotFoundException e) {
            return null;
        }
    }



}
