package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.CorsiDiStudioRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.ListCorsiDiStudioPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.ReadCorsoDiStudioPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CorsoDiStudioService {
    @Autowired
    private ListCorsiDiStudioPort listCorsiDiStudioPort;
    @Autowired
    private ReadCorsoDiStudioPort readCorsoDiStudioPort;

    public List<CorsoDiStudio> getCorsiDiStudio(){
        return this.listCorsiDiStudioPort.listCorsiDiStudio()
                .stream()
                .filter(corsoDiStudio -> !(corsoDiStudio.isProgrammato()))
                .collect(Collectors.toList());
    }

    public List<CorsoDiStudio> getCorsiDiStudio(String facolta) {
        return this.listCorsiDiStudioPort.listCorsiDiStudio()
                .stream()
                .filter(corsoDiStudio -> corsoDiStudio.getDenominazioneFacolta().toLowerCase().contains(facolta))
                .filter(corsoDiStudio -> !(corsoDiStudio.isProgrammato()))
                .collect(Collectors.toList());
    }


    public List<CorsoDiStudio> getCorsiDiStudioProgrammati(){
        return this.listCorsiDiStudioPort.listCorsiDiStudio()
                .stream()
                .filter(corsoDiStudio -> corsoDiStudio.isProgrammato())
                .collect(Collectors.toList());
    }

    public List<CorsoDiStudio> getCorsiDiStudioProgrammati(String facolta) {
        return this.listCorsiDiStudioPort.listCorsiDiStudio().stream()
                .filter(corsoDiStudio -> corsoDiStudio.getDenominazioneFacolta().toLowerCase().contains(facolta))
                .filter(corsoDiStudio -> corsoDiStudio.isProgrammato())
                .collect(Collectors.toList());
    }

    public Optional<CorsoDiStudio> getCorsoDiStudioById(String codice){
        return this.readCorsoDiStudioPort.findCorsoDiStudioById(codice);
    }
}
