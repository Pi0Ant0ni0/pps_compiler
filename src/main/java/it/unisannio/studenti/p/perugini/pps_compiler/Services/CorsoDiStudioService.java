package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.CorsiDiStudioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CorsoDiStudioService {
    @Autowired
    private CorsiDiStudioRepository corsiDiStudioRepository;

    public List<CorsoDiStudio> getCorsiDiStudio(){
        return this.corsiDiStudioRepository.findAll()
                .stream()
                .filter(corsoDiStudio -> !(corsoDiStudio.isProgrammato()))
                .collect(Collectors.toList());
    }

    public List<CorsoDiStudio> getCorsiDiStudio(String facolta) {
        return this.corsiDiStudioRepository.findAll().stream()
                .filter(corsoDiStudio -> corsoDiStudio.getDenominazioneFacolta().toLowerCase().contains(facolta))
                .filter(corsoDiStudio -> !(corsoDiStudio.isProgrammato()))
                .collect(Collectors.toList());
    }


    public List<CorsoDiStudio> getCorsiDiStudioProgrammati(){
        return this.corsiDiStudioRepository.findAll()
                .stream()
                .filter(corsoDiStudio -> corsoDiStudio.isProgrammato())
                .collect(Collectors.toList());
    }

    public List<CorsoDiStudio> getCorsiDiStudioProgrammati(String facolta) {
        return this.corsiDiStudioRepository.findAll().stream()
                .filter(corsoDiStudio -> corsoDiStudio.getDenominazioneFacolta().toLowerCase().contains(facolta))
                .filter(corsoDiStudio -> corsoDiStudio.isProgrammato())
                .collect(Collectors.toList());
    }

    public Optional<CorsoDiStudio> getCorsoDiStudioById(String codice){
        return this.corsiDiStudioRepository.findById(codice);
    }
}
