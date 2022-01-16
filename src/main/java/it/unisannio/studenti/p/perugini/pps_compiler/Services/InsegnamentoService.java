package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.CorsoDiStudioNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.InsegnamentoNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.CorsiDiStudioRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.AttivitaDidatticheRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.ManifestiDegliStudiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InsegnamentoService {
    @Autowired
    private CorsiDiStudioRepository corsiDiStudioRepository;
    @Autowired
    private AttivitaDidatticheRepository attivitaDidatticheRepository;
    @Autowired
    private ManifestiDegliStudiRepository manifestiDegliStudiRepository;

    public CorsoDiStudio getCorsoDiStudioByInsegnamento(AttivitaDidattica attivitaDidattica) throws CorsoDiStudioNotFoundException {
        Optional<CorsoDiStudio> corsoDiStudioOptional = this.corsiDiStudioRepository.findById(attivitaDidattica.getCodiceCorsoDiStudio());
        if (corsoDiStudioOptional.isPresent())
            return corsoDiStudioOptional.get();
        else{
            this.attivitaDidatticheRepository.delete(attivitaDidattica);
        }
        throw new CorsoDiStudioNotFoundException("L'insegnamento con codice: "+ attivitaDidattica.getCodiceAttivitaDidattica()+" ha un codice corso di studio che non trova riscontro nel database locale");

    }

    public List<AttivitaDidattica> getInsegnamentiPerCorsoDiStudio(String corsoDiStudio){
        return this.attivitaDidatticheRepository.findAll()
                .stream()
                .filter(insegnamento -> insegnamento.getCodiceCorsoDiStudio().equals(corsoDiStudio) && !insegnamento.isProgrammato())
                .collect(Collectors.toList());
    }

    public AttivitaDidattica getInsegnamentoById(String codiceInsegnamento) throws InsegnamentoNotFoundException {
        Optional<AttivitaDidattica> insegnamento = this.attivitaDidatticheRepository.findById(codiceInsegnamento);
        if (insegnamento.isPresent())
            return insegnamento.get();
        throw new InsegnamentoNotFoundException("Insegnamento non presente nel DB");
    }

    public List<AttivitaDidattica> getInsegnamenti() {
        return this.attivitaDidatticheRepository.findAll();
    }

    public List<AttivitaDidattica> getInsegnamentiProgrammatiPerCorsoDiStudio(String codiceCorsoDiStudio) {
        return this.attivitaDidatticheRepository.findAll()
                .stream()
                .filter(insegnamento -> insegnamento.getCodiceCorsoDiStudio().equals(codiceCorsoDiStudio) && insegnamento.isProgrammato())
                .collect(Collectors.toList());
    }

    public boolean exist(String codiceInsegnamento) {
        return  this.attivitaDidatticheRepository.findById(codiceInsegnamento).isPresent();
    }
}
