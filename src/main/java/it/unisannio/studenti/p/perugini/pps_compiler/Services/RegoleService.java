package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ManifestoDegliStudi;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.ManifestiDegliStudiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RegoleService {
    @Autowired
    private ManifestiDegliStudiRepository manifestiDegliStudiRepository;

    public List<Integer> getAnniRegole(String codiceCorsoDiStudio){
        List<ManifestoDegliStudi> regole= this.manifestiDegliStudiRepository.findAll();
        List<Integer> anni = new ArrayList<>();
        for(ManifestoDegliStudi manifestoDegliStudi : regole)
            if(manifestoDegliStudi.getChiaveManifestoDegliStudi().getCodiceCorsoDiStudio().equals(codiceCorsoDiStudio))
                anni.add(manifestoDegliStudi.getChiaveManifestoDegliStudi().getCoorte());
        return anni;
    }
}
