package it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.adapter;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.CorsiDiStudioRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.CreateCorsoDiStudioPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.DeleteCorsoDiStudioPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.ListCorsiDiStudioPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.ReadCorsoDiStudioPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CorsoDiStudioPortPortAdapter implements CreateCorsoDiStudioPort, ReadCorsoDiStudioPort, ListCorsiDiStudioPort, DeleteCorsoDiStudioPort {
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

    @Override
    public List<CorsoDiStudio> listCorsiDiStudio() {
        return this.corsiDiStudioRepository.findAll();
    }

    @Override
    public void deleteAll() {
        this.corsiDiStudioRepository.deleteAll();
    }
}
