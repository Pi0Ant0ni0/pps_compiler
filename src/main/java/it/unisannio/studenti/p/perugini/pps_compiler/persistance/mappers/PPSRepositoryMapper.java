package it.unisannio.studenti.p.perugini.pps_compiler.persistance.mappers;

import it.unisannio.studenti.p.perugini.pps_compiler.API.PPS;
import it.unisannio.studenti.p.perugini.pps_compiler.core.attivitaDidattica.port.ReadAttivitaDidatticaPort;
import it.unisannio.studenti.p.perugini.pps_compiler.persistance.entity.PPSEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PPSRepositoryMapper {
    @Autowired
    private ReadAttivitaDidatticaPort readAttivitaDidatticaPort;

    public PPS toDomain(PPSEntity entity) {
        PPS pps = new PPS();
        pps.setStudente(entity.getStudente());
        pps.setRifiutato(entity.isRifiutato());
        pps.setApprovato(entity.isApprovato());
        if (entity.getDataVisione().isPresent())
            pps.setDataVisione(entity.getDataVisione().get());
        if (entity.getOrientamento().isPresent())
            pps.setOrientamento(entity.getOrientamento().get()
                    .stream()
                    .map(s -> readAttivitaDidatticaPort.findAttivitaById(s).get())
                    .collect(Collectors.toList())
            );
        pps.setInsegnamentiASceltaLibera(entity.getInsegnamentiASceltaLibera()
                .stream()
                .map(s -> readAttivitaDidatticaPort.findAttivitaById(s).get())
                .collect(Collectors.toList())
        );

        if (entity.getCurriculum().isPresent())
            pps.setCurriculum(entity.getCurriculum().get());
        pps.setDataCompilazione(entity.getDataCompilazione());
        return pps;
    }

    public PPSEntity toEntity(PPS pps) {
        PPSEntity ppsEntity = new PPSEntity();
        ppsEntity.setApprovato(pps.isApprovato());
        ppsEntity.setRifiutato(pps.isRifiutato());
        ppsEntity.setDataCompilazione(pps.getDataCompilazione());
        ppsEntity.setStudente(pps.getStudente());
        ppsEntity.setInsegnamentiASceltaLibera(pps.getInsegnamentiASceltaLibera()
                .stream()
                .map(attivitaDidattica -> attivitaDidattica.getCodiceAttivitaDidattica())
                .collect(Collectors.toList())
        );
        if (pps.getCurriculum().isPresent())
            ppsEntity.setCurriculum(pps.getCurriculum().get());
        if (pps.getDataVisione().isPresent())
            ppsEntity.setDataVisione(pps.getDataVisione().get());
        if (pps.getOrientamento().isPresent())
            ppsEntity.setOrientamento(pps.getOrientamento().get()
                    .stream()
                    .map(attivitaDidattica -> attivitaDidattica.getCodiceAttivitaDidattica())
                    .collect(Collectors.toList())
            );
        return ppsEntity;
    }
}
