package it.unisannio.studenti.p.perugini.pps_compiler.persistance.mappers;

import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.ReadCorsoDiStudioPort;
import it.unisannio.studenti.p.perugini.pps_compiler.persistance.entity.AttivitaDidatticaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AttivitaDidatticheRepositoryMapper {
    @Autowired
    private ReadCorsoDiStudioPort readCorsoDiStudioPort;

    public AttivitaDidattica toDomain(AttivitaDidatticaEntity entity){
        return  new AttivitaDidattica(
                entity.getCodiceAttivitaDidattica(),
                entity.getDenominazioneAttivitaDidattica(),
                entity.getCfu(),
                readCorsoDiStudioPort.findCorsoDiStudioById(entity.getCodiceCorsoDiStudio()).get(),
                entity.isNonErogabile(),
                entity.getSettoreScientificoDisciplinare(),
                entity.isProgrammato(),
                entity.getContenuti(),
                entity.getMetodiDidattici(),
                entity.getModalitaVerificaApprendimento(),
                entity.getObiettivi(),
                entity.getPrerequisiti(),
                entity.getUnitaDidattiche().isPresent()?entity.getUnitaDidattiche().get():null
        );

    }

    public AttivitaDidatticaEntity toEntity(AttivitaDidattica domain){
        return new AttivitaDidatticaEntity(
                domain.getCodiceAttivitaDidattica(),
                domain.getDenominazioneAttivitaDidattica(),
                domain.getCfu(),
                domain.getCorsoDiStudio().getCodice(),
                domain.isNonErogabile(),
                domain.getSettoreScientificoDisciplinare(),
                domain.isProgrammato(),
                domain.getContenuti(),
                domain.getMetodiDidattici(),
                domain.getModalitaVerificaApprendimento(),
                domain.getObiettivi(),
                domain.getPrerequisiti(),
                domain.getUnitaDidattiche().isPresent()? domain.getUnitaDidattiche().get():null
        );

    }
}
