package it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.adapter;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.CorsiDiStudioRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.CreateCorsoDiStudioPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.ReadCorsoDiStudioPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CorsoDiStudioPortAdapter implements CreateCorsoDiStudioPort, ReadCorsoDiStudioPort {
    @Autowired
    private CorsiDiStudioRepository corsiDiStudioRepository;

    @Override
    public CorsoDiStudio save(CorsoDiStudio corsoDiStudio) {
        return this.corsiDiStudioRepository.save(corsoDiStudio);
    }

    @Override
    public Optional<CorsoDiStudio> findCorsoDiStudioById(String codice) {
        return this.corsiDiStudioRepository.findById(codice);
    }
}
