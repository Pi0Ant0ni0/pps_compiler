package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.*;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.InsegnamentoRegola;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.TipoCorsoDiLaurea;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.CorsoDiStudioNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.constants.ERR_MESSAGES;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.RegolaNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.TipoCorsoDiLaureaNonSupportatoException;
import it.unisannio.studenti.p.perugini.pps_compiler.core.attivitaDidattica.port.ListAttivitaDidattichePort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.ReadCorsoDiStudioPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port.ListManifestiDegliStudiPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port.ReadManifestoDegliStudiPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentiService {

    @Autowired
    private ListAttivitaDidattichePort listAttivitaDidattichePort;
    @Autowired
    private ReadCorsoDiStudioPort readCorsoDiStudioPort;
    @Autowired
    private ReadManifestoDegliStudiPort readManifestoDegliStudiPort;
    @Autowired
    private AttivitaDidatticaService attivitaDidatticaService;
    @Autowired
    private ListManifestiDegliStudiPort listManifestiDegliStudiPort;


    public List<AttivitaDidattica> getFreeChoiceCourses(String codiceCorsoDiStudio, int coorte, String curriculum) throws CorsoDiStudioNotFoundException, RegolaNotFoundException, TipoCorsoDiLaureaNonSupportatoException {
        List<AttivitaDidattica> insegnamentiLiberi = listAttivitaDidattichePort.listAttivitaDidatticheSceltaLibera(codiceCorsoDiStudio);
        Optional<CorsoDiStudio>corsoDiStudioStudente =this.readCorsoDiStudioPort.findCorsoDiStudioById(codiceCorsoDiStudio);
        if(!corsoDiStudioStudente.isPresent())
            throw new CorsoDiStudioNotFoundException(ERR_MESSAGES.CORSO_NOT_FOUND+codiceCorsoDiStudio);
        ChiaveManifestoDegliStudi chiaveManifestoDegliStudi = new ChiaveManifestoDegliStudi();
        chiaveManifestoDegliStudi.setCoorte(coorte);
        chiaveManifestoDegliStudi.setCodiceCorsoDiStudio(codiceCorsoDiStudio);
        if(curriculum.length()!=0)
            chiaveManifestoDegliStudi.setCurricula(curriculum);
        else chiaveManifestoDegliStudi.setCurricula(null);
        Optional<ManifestoDegliStudi> regolaOptional = this.readManifestoDegliStudiPort.findManifestoById(chiaveManifestoDegliStudi);
        if (!regolaOptional.isPresent())
            throw new RegolaNotFoundException(ERR_MESSAGES.MANIFESTO_NOT_FOUND+coorte+", "+codiceCorsoDiStudio);

        //li inserisco tutti in una lista, non mi interessa l'anno
        List<InsegnamentoRegola> insegnamentiObbligatori = new ArrayList<>();
        Map<Integer, AnnoAccademico> schemiDiPiano = regolaOptional.get().getAnniAccademici();
        for (Integer anno : schemiDiPiano.keySet())
            insegnamentiObbligatori.addAll(schemiDiPiano.get(anno).getInsegnamentiObbligatori());


        //elimino gli insegnamenti che erano obbligatori per la coorte
        //elimino gli insegnamenti che sono di corsi di laurea che non posso scegliere
        List<AttivitaDidattica> buffer = new ArrayList<>();
        buffer.addAll(insegnamentiLiberi);
        for(InsegnamentoRegola obbligatorio : insegnamentiObbligatori) {
            for (AttivitaDidattica libero : buffer) {
                if (obbligatorio.getDenominazioneInsegnamento().equals(libero.getDenominazioneAttivitaDidattica()))
                    insegnamentiLiberi.remove(libero);
            }
        }

        buffer = new ArrayList<>();
        buffer.addAll(insegnamentiLiberi);
        for(AttivitaDidattica libero: buffer){
            //recupero il corso di studio
            CorsoDiStudio corsoDiStudio = this.attivitaDidatticaService.getCorsoDiStudioByInsegnamento(libero);
            //un triennale puo scegliere solo triennale
            if (corsoDiStudioStudente.get().getTipoCorsoDiLaurea().equals(TipoCorsoDiLaurea.L2) &&
                    corsoDiStudio.getTipoCorsoDiLaurea().equals(TipoCorsoDiLaurea.LM))
            {
                insegnamentiLiberi.remove(libero);
            }

            //un magistrale puo scegliere solo magistrale
            if (corsoDiStudioStudente.get().getTipoCorsoDiLaurea().equals(TipoCorsoDiLaurea.LM) &&
                    corsoDiStudio.getTipoCorsoDiLaurea().equals(TipoCorsoDiLaurea.L2))
            {
                insegnamentiLiberi.remove(libero);
            }
        }
        return insegnamentiLiberi;
    }


    public List<String> getCurricula(String codiceCorsoDiStudio, int coorte) {
       return this.listManifestiDegliStudiPort.list()
               .stream()
               .filter(manifestoDegliStudi -> manifestoDegliStudi.getChiaveManifestoDegliStudi().getCodiceCorsoDiStudio().equals(codiceCorsoDiStudio))
               .filter(manifestoDegliStudi -> manifestoDegliStudi.getChiaveManifestoDegliStudi().getCoorte()==coorte)
               .filter(manifestoDegliStudi -> manifestoDegliStudi.getChiaveManifestoDegliStudi().getCurricula().isPresent())
               .map(manifestoDegliStudi -> manifestoDegliStudi.getChiaveManifestoDegliStudi().getCurricula().get())
               .collect(Collectors.toList());

    }
}
